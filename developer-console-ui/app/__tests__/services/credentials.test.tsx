import { getStoredUsers, loginUser, logoutUser, signupUser } from "../../services/credentials.service";

describe('credentials', () => {
    beforeEach(() => {
        localStorage.clear();
    });

    it('logs in with the default developer account and keeps role', () => {
        const result = loginUser('developer', 'password');
        expect(result.success).toBeTruthy();
        expect(result.username).toBe('developer');
        expect(result.role).toBe('developer');
        expect(localStorage.getItem('role')).toBe('developer');
    });

    it('creates and logs in a new dummy user', () => {
        const signup = signupUser('new-user', 'secret', 'manager');
        expect(signup.success).toBeTruthy();
        expect(signup.role).toBe('manager');

        logoutUser();
        const login = loginUser('new-user', 'secret');
        expect(login.success).toBeTruthy();
        const users = getStoredUsers();
        expect(users.some((user) => user.username === 'new-user' && user.role === 'manager')).toBeTruthy();
        expect(localStorage.getItem('role')).toBe('manager');
    });
});
