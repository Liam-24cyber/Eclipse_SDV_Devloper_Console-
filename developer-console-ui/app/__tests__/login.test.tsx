import { fireEvent, render, screen } from '@testing-library/react';
jest.mock('next/router', () => require('next-router-mock'));
import router from 'next-router-mock';
import { store } from '../services/store.service';
import { StoreProvider } from 'easy-peasy';
import Login from '../pages/login';
describe('login ', () => {
  beforeEach(() => {
    localStorage.clear();
    router.push('/login');
  });

  it("logs in with default credentials", () => {
    render(
      //  @ts-ignore 
      <StoreProvider store={store}>
        <Login />
      </StoreProvider>
    );
    const submit = screen.getByTestId("submitBtn");
    fireEvent.click(submit);

    expect(router.asPath).toBe('/dco/scenario');
    expect(localStorage.getItem('role')).toBe('developer');
  });
})
