import { Box, Flex, Headline, StatusMessage, Table, Value } from "@dco/sdv-ui";
import { useStoreActions } from "easy-peasy";
import { useEffect, useState } from "react";
import { useRouter } from "next/router";
import Dco from "..";
import { ApprovalRequest, listApprovals } from "../../../services/approvals.service";

const ApprovalsPage = () => {
  const setCount = useStoreActions((actions: any) => actions.setCount);
  const [approvals, setApprovals] = useState<ApprovalRequest[]>([]);
  const router = useRouter();

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

  const columns = [
    { Header: 'Title', accessor: 'name' },
    { Header: 'Created By', accessor: 'createdBy' },
    { Header: 'Status', accessor: 'status' },
    { Header: 'Scenario Type', accessor: 'scenarioType' },
    { Header: 'Environment', accessor: 'environment' },
    { Header: 'Platform', accessor: 'platform' },
    { Header: 'Required compute', accessor: 'requiredCompute' },
    {
      Header: 'Submitted',
      accessor: 'createdAt',
      formatter: (_: any, cell: any) => {
        const ts = cell?.row?.values?.createdAt || cell?.row?.original?.createdAt;
        const date = ts ? new Date(ts) : null;
        return date ? date.toLocaleString() : '';
      }
    },
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
      </Box>
    </Dco>
  );
};

export default ApprovalsPage;
