import { ApolloClient, HttpLink, InMemoryCache } from '@apollo/client'
import { setContext } from '@apollo/client/link/context';
import getConfig from 'next/config';

// Prefer a configured gateway URL so the UI works both locally and in Docker
const { publicRuntimeConfig } = getConfig() || {};
const runtimeEnv = typeof process !== 'undefined' ? process.env : undefined;
const isBrowser = typeof window !== 'undefined';
const browserGatewayBaseUrl = isBrowser
  ? `${window.location.protocol}//${window.location.hostname}:8080`
  : undefined;
const gatewayBaseUrl =
  browserGatewayBaseUrl ||
  runtimeEnv?.NEXT_PUBLIC_DCO_GATEWAY_URL ||
  runtimeEnv?.APP_DCO_GATEWAY_SERVICE_URL ||
  publicRuntimeConfig?.url ||
  'http://localhost:8080';

const graphqlUrl = `${gatewayBaseUrl.replace(/\/$/, '')}/graphql`;

const httpLink = new HttpLink({ uri: graphqlUrl, fetch });

export const Link = graphqlUrl;

const authLink = setContext((_, { headers }) => {
  // get the authentication token from local storage if it exists
  const token = localStorage.getItem('token');
  // return the headers to the context so httpLink can read them
  return {
    headers: {
      ...headers,
      'Authorization': token ? `Basic ${token}` : "",
    }
  }
});

const client = new ApolloClient({
  link: authLink.concat(httpLink),
  cache: new InMemoryCache()
});

export default client
