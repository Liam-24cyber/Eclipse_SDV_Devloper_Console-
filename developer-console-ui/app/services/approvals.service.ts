type ApprovalStatus = 'pending' | 'approved' | 'rejected';

export type ApprovalRequest = {
  id: string;
  name: string;
  description: string;
  environment: string;
  platform: string;
  scenarioType: string;
  hardware: string;
  tracks: string[];
  scenarios: string[];
  createdBy: string;
  requiredCompute: string;
  status: ApprovalStatus;
  createdAt: string;
};

const APPROVALS_KEY = 'sdv-approvals';

const saveApprovals = (approvals: ApprovalRequest[]) => {
  localStorage.setItem(APPROVALS_KEY, JSON.stringify(approvals));
};

const makeId = () => {
  const randomPart = Math.random().toString(36).substring(2, 8);
  const timePart = Date.now().toString(36);
  return `${timePart}-${randomPart}`;
};

export const listApprovals = (): ApprovalRequest[] => {
  const raw = localStorage.getItem(APPROVALS_KEY);
  if (!raw) return [];
  try {
    const parsed = JSON.parse(raw);
    if (Array.isArray(parsed)) return parsed;
    return [];
  } catch (e) {
    console.warn('Failed to parse approvals', e);
    return [];
  }
};

export const createApprovalRequest = (input: Omit<ApprovalRequest, 'id' | 'createdAt'>) => {
  const approvals = listApprovals();
  const next: ApprovalRequest = {
    ...input,
    id: makeId(),
    createdAt: new Date().toISOString(),
  };
  approvals.unshift(next);
  saveApprovals(approvals);
  return next;
};
