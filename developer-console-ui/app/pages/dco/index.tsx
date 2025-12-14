import { Flex, Box, Headline, Button, NavigationBar, NavigationBarItem, StatusMessage, Spacer } from '@dco/sdv-ui'
import { useStoreActions, useStoreState } from 'easy-peasy'
import { useRouter } from 'next/router'
import { useState } from 'react'
import ActiveLink from '../../components/layout/ActiveLink'
import Layout from '../../components/layout/layout'
import { checkRoute, invert } from '../../services/functionShared'
import { onClickNewSimulation } from '../../services/functionSimulation.service'
import { onClickNewTrack } from '../../services/functionTrack.service'
import NewScenario from './addEditScenario/newScenario'
export function Count() {
  return useStoreState((state: any) => state.count)
}
// main landing page 
const Dco = ({ children }: any) => {
  const router = useRouter()
  const [showScenarioPopup, setShowScenarioPopup] = useState(false)
  const setCompid = useStoreActions((actions: any) => actions.setCompid)
  const pathname = router?.pathname
  const trackTabClicked = router.pathname === '/dco/tracksMain'
  const libTabClicked = router.pathname.includes('/dco/scenario')
  const simulationTabClicked = router.pathname === '/dco/simulation'
  const approvalsTabClicked = router.pathname === '/dco/approvals'
  const resultsTabClicked = router.pathname === '/dco/results'
  const token = localStorage.getItem('token')
  const role = (localStorage.getItem('role') || 'developer').toLowerCase()
  const isDeveloper = role === 'developer'
  return (<>{ 
    <Layout>
      <Box padding='none' invert={invert()} variant='body' fullHeight>
        <Flex column fullHeight>
          {token ? <> <Flex.Item autoSize>
            {/* Header */}
            <Flex valign='bottom'>
              {/* HEADLINE */}
              <Flex.Item flex={1} textAlign='right'>
                <Box padding='sidebar'>
                  <Flex gutters='small'>
                    <Flex.Item autoSize>
                      {libTabClicked && <Headline level={1}> {Count() || 0} scenarios</Headline>}
                      {trackTabClicked && <Headline level={1}>{Count() || 0} tracks</Headline>}
                      {simulationTabClicked && <Headline level={1}> {Count() || 0} simulations</Headline>}
                      {(!isDeveloper && approvalsTabClicked) && <Headline level={1}> {Count() || 0} approvals</Headline>}
                      {resultsTabClicked && <Headline level={1}> {Count() || 0} results</Headline>}
                    </Flex.Item>
                    <Flex.Item textAlign='left' valign='bottom'>
                      {libTabClicked && (<Button style={{ marginTop: '-.3em' }} data-testid='newReleaseBtn'
                        onClick={() => { setShowScenarioPopup(true) }}>
                        New Scenario
                      </Button>)}
                      {trackTabClicked && (<Button style={{ marginTop: '-.3em' }} data-testid='addTrackbtn'
                        onClick={() => onClickNewTrack(setCompid)}>
                        New Track
                      </Button>)}
                      {simulationTabClicked && (<Button style={{ marginTop: '-.3em' }} data-testid='addSimulationBtn'
                        onClick={() => onClickNewSimulation()}>
                        New Simulation
                      </Button>)}
                      {resultsTabClicked && (
                        <div style={{ marginTop: '-.3em', color: '#666', fontSize: '14px', padding: '8px 0' }}>
                          Results are generated automatically from simulations
                        </div>
                      )}
                    </Flex.Item>
                  </Flex>
                </Box>
              </Flex.Item>
              <NewScenario show={showScenarioPopup} onClose={setShowScenarioPopup} path='create' />
              {/* NAV */}
              <Flex.Item autoSize>
                <NavigationBar>
                  <ActiveLink href={checkRoute('/dco/scenario', router, pathname)}>
                    <NavigationBarItem>scenario</NavigationBarItem>
                  </ActiveLink>
                  <ActiveLink href={checkRoute('/dco/tracksMain', router, pathname)}>
                    <NavigationBarItem>tracks</NavigationBarItem>
                  </ActiveLink>
                  <ActiveLink href={checkRoute('/dco/simulation', router, pathname)}>
                    <NavigationBarItem>simulations</NavigationBarItem>
                  </ActiveLink>
                  {!isDeveloper && (
                    <ActiveLink href={checkRoute('/dco/approvals', router, pathname)}>
                      <NavigationBarItem>approvals</NavigationBarItem>
                    </ActiveLink>
                  )}
                  <ActiveLink href={checkRoute('/dco/results', router, pathname)}>
                    <NavigationBarItem>results</NavigationBarItem>
                  </ActiveLink>
                </NavigationBar>
              </Flex.Item>
              {/* RIGHT */}
              <Flex.Item flex={1} textAlign='right'>
              </Flex.Item>
            </Flex>
          </Flex.Item>
            <Flex.Item>
              <Flex fullHeight>
                <Flex.Item>
                  <Box fullHeight variant='high'>{children}</Box>
                </Flex.Item>
              </Flex>
            </Flex.Item>
          </>
            :
            <>
              <Flex.Item align="center" autoSize >
                <Flex>
                  <Flex.Item>
                    <Spacer space={10}></Spacer>
                    <Spacer space={5}></Spacer>
                    <Box interactive variant='high' padding='large' elevation='low'>
                      <Flex rows={1} gutters="small">
                        <Flex.Item  valign='center'> <StatusMessage noIcon variant='secondary'>Session has been expired click
                        </StatusMessage></Flex.Item>
                        <Flex.Item > <Button  variant='primary' size='small' align='right' onClick={() => { router.replace('/login') }}> here </Button>
                          </Flex.Item>
                        <Flex.Item valign='center'>     <StatusMessage noIcon variant='secondary' >to login again
                        </StatusMessage></Flex.Item>
                      </Flex>


                    </Box>
                  </Flex.Item>
                </Flex>
              </Flex.Item>
            </>}
        </Flex>
      </Box>
    </Layout>

  }

  </>)
}
export default Dco
