import { Box, Button, Flex, Headline, StatusMessage, Table, Toast, Value } from "@dco/sdv-ui";
import { useStoreActions } from "easy-peasy";
import { useEffect, useState } from "react";
import { useRouter } from "next/router";
import { useMutation } from "@apollo/client";
import Dco from "..";
import { ApprovalRequest, listApprovals, resolveApproval } from "../../../services/approvals.service";
import { LAUNCH_SIMULATION } from "../../../services/queries";
import BoxToast from "../../../components/layout/boxToast";

const ApprovalsPage = () => {
  const setCount = useStoreActions((actions: any) => actions.setCount);
  const [approvals, setApprovals] = useState<ApprovalRequest[]>([]);
  const router = useRouter();
  const [isToastOpen, setIsToastOpen] = useState(false);
  const [toastMsg, setToastMsg] = useState<string>('');
  const [pendingId, setPendingId] = useState<string | null>(null);

  const [approveSimulation] = useMutation(LAUNCH_SIMULATION, {
    onCompleted() {
      setToastMsg('Simulation launched from approval.');
      setIsToastOpen(true);
      if (pendingId) {
        const refreshed = resolveApproval(pendingId, 'approved');
        setApprovals(refreshed);
        setCount(refreshed.length);
        setPendingId(null);
      }
    },
    onError(error) {
      console.error('Launch from approval failed', error);
      setToastMsg('Failed to launch simulation from approval.');
      setIsToastOpen(true);
      setPendingId(null);
    }
  });

  useEffect(() => {
    const role = (localStorage.getItem('role') || 'developer').toLowerCase();
    if (role === 'developer') {
      router.replace('/dco/simulation');
      return;
    }
  }, [router]);

  useEffect(() => {
    const current = listApprovals();
    setApprovals(current);
    setCount(current.length);
  }, [setCount]);

  const handleApprove = (request: ApprovalRequest) => {
    setPendingId(request.id);
    approveSimulation({
      variables: {
        simulationInput: {
          name: request.name,
          environment: request.environment,
          description: request.description,
          platform: request.platform,
          scenarioType: request.scenarioType,
          hardware: request.hardware,
          tracks: request.tracks,
          scenarios: request.scenarios,
          createdBy: request.createdBy || 'team-lead',
        },
      },
    });
  };

  const columns = [
    { Header: 'Title', accessor: 'name' },
    { Header: 'Created By', accessor: 'createdBy' },
    { Header: 'Status', accessor: 'status' },
    { Header: 'Scenario Type', accessor: 'scenarioType' },
    { Header: 'Environment', accessor: 'environment' },
    { Header: 'Platform', accessor: 'platform' },
    { Header: 'Required compute', accessor: 'requiredCompute' },
    {
      Header: 'Estimated compute',
      accessor: 'estimatedComputeUnits',
      formatter: (_: any, cell: any) => `${cell?.row?.values?.estimatedComputeUnits ?? '—'} units`,
    },
    {
      Header: 'Est. runtime',
      accessor: 'estimatedRuntimeMinutes',
      formatter: (_: any, cell: any) => `${cell?.row?.values?.estimatedRuntimeMinutes ?? '—'} min`,
    },
    {
      Header: 'Submitted',
      accessor: 'createdAt',
      formatter: (_: any, cell: any) => {
        const ts = cell?.row?.values?.createdAt || cell?.row?.original?.createdAt;
        const date = ts ? new Date(ts) : null;
        return date ? date.toLocaleString() : '';
      }
    },
    {
      Header: 'Actions',
      accessor: 'actions',
      formatter: (_: any, cell: any) => {
        const req = cell?.row?.original as ApprovalRequest;
        return (
          <Button size="small" onClick={() => handleApprove(req)}>
            Approve & Launch
          </Button>
        );
      }
    }
  ];

  return (
    <Dco>
      <Box padding="large">
        <Flex justify="space-between" align="center">
          <Headline level={1}>Approvals</Headline>
          <Value>{approvals.length} pending</Value>
        </Flex>
        <Box padding="small">
          {approvals.length === 0 ? (
            <StatusMessage variant="secondary">No approval requests yet.</StatusMessage>
          ) : (
            // @ts-ignore
            <Table columns={columns} data={approvals} />
          )}
        </Box>
        <Toast show={isToastOpen}>
          <BoxToast toastMsg={toastMsg} />
        </Toast>
      </Box>
    </Dco>
  );
};

export default ApprovalsPage;
