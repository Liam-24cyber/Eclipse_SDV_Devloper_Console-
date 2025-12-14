import { Box, Button, Flex, Headline, Icon, Spacer, StatusMessage, Value } from "@dco/sdv-ui"
import { useStoreActions, useStoreState } from "easy-peasy"
import { useRouter } from "next/router";
import { useEffect } from "react";
import Layout from "../../components/layout/layout";
import { logoutUser } from "../../services/credentials.service";
export function Error() {
    const invert = useStoreState((state: any) => state.invert)
    const setUser = useStoreActions((actions: any) => actions.setUser)
    const router = useRouter();

    useEffect(() => {
        logoutUser();
        setUser(false);
    }, [setUser]);

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
                                <Value>Something went wrong with your session.</Value>
                            </Box>
                            <Box variant="body" transparency="high" padding="large" >
                                <StatusMessage variant="error">
                                    You have been signed out. Start again with the quick login.
                                </StatusMessage>
                                <Spacer space={4}></Spacer>
                                <Button data-testid="loginBtn" width="compact" onClick={() => { router.replace('/login') }}>Back to login</Button>
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
export default Error

