import { Link } from "../libs/apollo"
import { GET_SCENARIO } from "./queries"

export const onSubmit = async (userName: any, pass: any, setUser: Function, router: any) => {
    try {
        let username = userName.current
        let password = pass.current
        console.log('Login attempt with:', { username, passwordLength: password?.length });
        
        if (!username || !password) {
            console.error('Missing username or password');
            router.replace('/error');
            return;
        }
        
        var decodedStringBtoA = `${username}:${password}`
        var encodedStringBtoA = btoa(decodedStringBtoA) 
        console.log('Base64 encoded credentials:', encodedStringBtoA);
        
        localStorage.setItem('token', encodedStringBtoA);
        localStorage.setItem('user', userName.current);
        
        const token = localStorage.getItem('token');
        const url = 'http://localhost:8080/graphql';
        console.log('Making request to:', url);
        console.log('Authorization header:', `Basic ${token}`);
        
        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'content-type': 'application/json',
                'Authorization': token ? `Basic ${token}` : "",
            },
            body: JSON.stringify({
                query: GET_SCENARIO,
                variables: {
                    scenarioPattern: '',
                    page: 0,
                    size: 10,
                },
            }),
        });
        
        console.log('Response status:', response.status);
        console.log('Response headers:', Object.fromEntries(response.headers.entries()));
        
        if (!response.ok) {
            console.error('HTTP error:', response.status, response.statusText);
            router.replace('/error');
            localStorage.removeItem('token');
            setUser(false);
            return;
        }
        
        const result = await response.json();
        console.log('Response data:', result);
        
        if (result.errors) {
            console.error('GraphQL errors:', result.errors);
            router.replace('/error');
            localStorage.removeItem('token');
            setUser(false);
            return;
        }
        
        return result;
    } catch (error) {
        console.error('Login error:', error);
        router.replace('/error');
        localStorage.removeItem('token');
        setUser(false);
        throw error;
    }
}