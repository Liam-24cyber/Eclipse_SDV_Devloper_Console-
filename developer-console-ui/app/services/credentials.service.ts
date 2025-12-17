export type Role = 'developer' | 'team-lead' | 'manager';

type StoredUser = {
    username: string;
    password: string;
    role: Role;
};

type AuthResult = {
    success: boolean;
    message?: string;
    username?: string;
    role?: Role;
    token?: string;
};

const USER_STORE_KEY = 'sdv-dev-users';
const DEFAULT_USERS: StoredUser[] = [{ username: 'developer', password: 'password', role: 'developer' }];
const ROLE_KEY = 'role';

const saveUsers = (users: StoredUser[]) => {
    localStorage.setItem(USER_STORE_KEY, JSON.stringify(users));
};

const ensureDefaultUsers = (): StoredUser[] => {
    const rawUsers = localStorage.getItem(USER_STORE_KEY);
    if (!rawUsers) {
        saveUsers(DEFAULT_USERS);
        return DEFAULT_USERS;
    }

    try {
        const parsed: StoredUser[] = JSON.parse(rawUsers);
        if (!Array.isArray(parsed)) {
            saveUsers(DEFAULT_USERS);
            return DEFAULT_USERS;
        }

        const normalized = parsed.map((user) => ({
            ...user,
            role: (user.role as Role) || 'developer',
        }));

        const hasDefaultUser = normalized.some(
            (user) => user.username.toLowerCase() === DEFAULT_USERS[0].username.toLowerCase()
        );

        if (!hasDefaultUser) {
            const updated = [DEFAULT_USERS[0], ...normalized];
            saveUsers(updated);
            return updated;
        }

        saveUsers(normalized);
        return normalized;
    } catch (error) {
        console.warn('Unable to parse stored users, resetting to defaults', error);
        saveUsers(DEFAULT_USERS);
        return DEFAULT_USERS;
    }
};

const persistSession = (username: string, password: string, role: Role) => {
    const token = btoa(`${username}:${password}`);
    localStorage.setItem('token', token);
    localStorage.setItem('user', username);
    localStorage.setItem(ROLE_KEY, role);
    return token;
};

export const loginUser = (usernameInput: string, passwordInput: string): AuthResult => {
    const username = usernameInput?.trim();
    const password = passwordInput;

    if (!username || !password) {
        return { success: false, message: 'Username and password are required.' };
    }

    const users = ensureDefaultUsers();
    const match = users.find((user) => user.username.toLowerCase() === username.toLowerCase());

    if (!match || match.password !== password) {
        return { success: false, message: 'Invalid username or password.' };
    }

    const token = persistSession(match.username, match.password, match.role || 'developer');
    return { success: true, username: match.username, token, role: match.role || 'developer' };
};

export const signupUser = (usernameInput: string, passwordInput: string, roleInput: Role): AuthResult => {
    const username = usernameInput?.trim();
    const password = passwordInput;
    const role = roleInput || 'developer';

    if (!username || !password) {
        return { success: false, message: 'Pick a username and password to continue.' };
    }

    const users = ensureDefaultUsers();
    const exists = users.some((user) => user.username.toLowerCase() === username.toLowerCase());
    if (exists) {
        return { success: false, message: 'That username already exists.' };
    }

    const nextUsers = [...users, { username, password, role }];
    saveUsers(nextUsers);

    const token = persistSession(username, password, role);
    return { success: true, username, token, role, message: 'Account created. You are now logged in.' };
};

export const logoutUser = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    localStorage.removeItem(ROLE_KEY);
};

export const getStoredUsers = () => ensureDefaultUsers();

export const roles: { value: Role; label: string }[] = [
    { value: 'developer', label: 'Developer' },
    { value: 'team-lead', label: 'Team Lead' },
    { value: 'manager', label: 'Manager' },
];