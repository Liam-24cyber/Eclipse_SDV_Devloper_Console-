import { fireEvent, render, screen } from '@testing-library/react';
jest.mock('next/router', () => require('next-router-mock'));
import router from 'next-router-mock';
import { store } from '../services/store.service';
import { StoreProvider } from 'easy-peasy';
import Error from '../pages/error';

describe('error page', () => {
  beforeEach(() => {
    localStorage.clear();
    router.push('/error');
  });

  it("should render error page and navigate back to login", async () => {
    render(
      //  @ts-ignore 
      <StoreProvider store={store}>
        <Error />
      </StoreProvider>
    );

    const loginBtn = screen.getByTestId("loginBtn");
    fireEvent.click(loginBtn);
    expect(router.asPath).toBe('/login');
  });
});
