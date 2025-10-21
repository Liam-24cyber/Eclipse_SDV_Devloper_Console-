import { action, createStore, persist } from "easy-peasy";

export const store = createStore(persist({
  invert: true,
  setInvert: action((state: any, payload: any) => {
    state.invert = payload;
  }),
  count: 0,
  setCount: action((state: any, payload: any) => {
    state.count = payload;
  }),
  tname: 'Track',
  setTname: action((state: any, payload: any) => {
    state.tname = payload;
  }),
  tid: '0',
  setTid: action((state: any, payload: any) => {
    state.tid = payload;
  }),
  compid: '',
  setCompid: action((state: any, payload: any) => {
    state.compid = payload;
  }),
  
  page: '',
  setPage: action((state: any, payload: any) => {
    state.setPage = payload;
  }),
  selectedscenario: [{id:'93b866de-a642-4543-886c-a3597dbe9d8f',checked:false}],
  setSelectedscenario: action((state: any, payload: any) => {
      state.selectedscenario = payload;
  }),
  selectedtrack: [{id:'a633a44b-0df6-43c5-9250-aaca94191054',checked:false}],
  setSelectedtrack: action((state: any, payload: any) => {
      state.selectedtrack = payload;
  }),
  searchval:"",
  setSearchval: action((state: any, payload: any) => {
    state.searchval = payload;
  }),
  user:false,
  setUser:action((state: any, payload: any) => {
    state.user = payload;
  }),
}));
