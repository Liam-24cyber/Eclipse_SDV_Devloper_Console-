import { Box, Button, Flex, Headline, Icon, Input, Spacer, StatusMessage, Value } from "@dco/sdv-ui"
import { useStoreActions, useStoreState } from "easy-peasy"
import { useRouter } from "next/router";
import { useEffect, useMemo, useState } from "react";
import Layout from "../../components/layout/layout";
import { Role, getStoredUsers, loginUser, logoutUser, roles, signupUser } from "../../services/credentials.service";

type AuthMode = 'login' | 'signup';

export function Login() {
    const invert = useStoreState((state: any) => state.invert)
    const setUser = useStoreActions((actions: any) => actions.setUser)
    const router = useRouter();

    const [mode, setMode] = useState<AuthMode>('login');
    const [username, setUsername] = useState('developer');
    const [password, setPassword] = useState('password');
    const [role, setRole] = useState<Role>('developer');
    const [message, setMessage] = useState<{ variant: 'success' | 'error', text: string } | null>(null);

    useEffect(() => {
        logoutUser();
        setUser(false);
        getStoredUsers();
    }, [setUser]);

    const headlineCopy = useMemo(() => mode === 'login'
        ? 'Quick login for developers'
        : 'Create a dummy account in seconds', [mode]);

    const handleSubmit = () => {
        setMessage(null);
        const result = mode === 'login'
            ? loginUser(username, password)
            : signupUser(username, password, role);

        if (result.success) {
            setUser(true);
            setMessage({ variant: 'success', text: result.message || (mode === 'login' ? 'Logged in successfully.' : 'Account created. Logged in.') });
            router.replace('/dco/scenario');
            return;
        }

        setUser(false);
        setMessage({ variant: 'error', text: result.message || 'Please try again.' });
    }

    return (
        <Layout>
            <Box fullHeight invert={invert} variant='high' padding="large">
                <Spacer space={5} />
                <Box padding="large" >
                    <Flex justify="center">
                        <Flex.Item>
                        </Flex.Item>
                        <Flex.Item justify-content="center" textAlign="center" align="center" >
                            <Box variant="body" padding="large" >
                                <Headline level={2}>Welcome to SDV Developer Console  <Icon name='tbbui'></Icon></Headline>
                                <Value>{headlineCopy}</Value>
                            </Box>
                            <Box variant="body" transparency="high" padding="large" >
                                {message ? (
                                    <>
                                        <StatusMessage variant={message.variant} noIcon>
                                            {message.text}
                                        </StatusMessage>
                                        <Spacer />
                                    </>
                                ) : null}
                                <Flex column>
                                    <Flex.Item>
                                        <Input
                                            withAccessory
                                            accessoryIcon="user"
                                            label="Username"
                                            value={username}
                                            onValueChange={(value) => setUsername(value)}
                                        />
                                    </Flex.Item>
                                    <Spacer />
                                    <Flex.Item>
                                        <Input
                                            withAccessory
                                            accessoryIcon="lock"
                                            type="password"
                                            label="Password"
                                            value={password}
                                            onValueChange={(value) => setPassword(value)}
                                        />
                                    </Flex.Item>
                                    {mode === 'signup' && (
                                        <>
                                            <Spacer />
                                            <Flex.Item>
                                                <Value>Select a role for this account</Value>
                                                <Spacer space={1} />
                                                <Flex justify="center">
                                                    {roles.map((option) => (
                                                        <Flex.Item key={option.value}>
                                                            <Button
                                                                width="compact"
                                                                variant={role === option.value ? 'primary' : 'naked'}
                                                                onClick={() => setRole(option.value)}
                                                            >
                                                                {option.label}
                                                            </Button>
                                                            <Spacer space={1} />
                                                        </Flex.Item>
                                                    ))}
                                                </Flex>
                                            </Flex.Item>
                                        </>
                                    )}
                                    <Spacer space={4} />
                                    <Flex.Item textAlign="center">
                                        <Button data-testid="submitBtn" width="full" onClick={handleSubmit}>
                                            {mode === 'login' ? 'Login' : 'Create account'}
                                        </Button>
                                    </Flex.Item>
                                    <Spacer />
                                    <Flex.Item textAlign="center">
                                        <Button width="full" onClick={() => {
                                            setMode(mode === 'login' ? 'signup' : 'login');
                                            setMessage(null);
                                        }}>
                                            {mode === 'login' ? 'Need an account? Sign up' : 'Already have an account? Login'}
                                        </Button>
                                    </Flex.Item>
                                </Flex>
                            </Box>
                        </Flex.Item>
                        <Flex.Item>
                        </Flex.Item>
                    </Flex>
                </Box>
            </Box >
        </Layout>
    )
}
export default Login
