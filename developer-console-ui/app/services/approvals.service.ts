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
  estimatedComputeUnits: number;
  estimatedRuntimeMinutes: number;
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

const computeEstimate = (input: {
  environment?: string;
  platform?: string;
  scenarioType?: string;
  hardware?: string;
}) => {
  const base = 10;

  const env = (input.environment || '').toLowerCase();
  const environmentScore = env.includes('prod') ? 10 : env.includes('qa') ? 5 : 0;

  const platform = (input.platform || '').toLowerCase();
  const platformScore = platform.includes('hil') ? 15 : (platform.includes('sil') || platform.includes('sim')) ? 5 : 0;

  const scenario = (input.scenarioType || '').toLowerCase();
  const scenarioScore = scenario.includes('fleet') ? 15 : scenario.includes('sensor') ? 10 : 5;

  const hardware = (input.hardware || '').toLowerCase();
  const hardwareScore = hardware.includes('gpu') ? 15 : (hardware.includes('high') || hardware.includes('xlarge')) ? 10 : 5;

  const estimatedComputeUnits = base + environmentScore + platformScore + scenarioScore + hardwareScore;
  const estimatedRuntimeMinutes = Math.min(60, Math.max(5, 5 + Math.floor(estimatedComputeUnits / 10) * 5));

  return { estimatedComputeUnits, estimatedRuntimeMinutes };
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
  const { estimatedComputeUnits, estimatedRuntimeMinutes } = computeEstimate({
    environment: input.environment,
    platform: input.platform,
    scenarioType: input.scenarioType,
    hardware: input.hardware,
  });
  const next: ApprovalRequest = {
    ...input,
    estimatedComputeUnits,
    estimatedRuntimeMinutes,
    id: makeId(),
    createdAt: new Date().toISOString(),
  };
  approvals.unshift(next);
  saveApprovals(approvals);
  return next;
};

export const resolveApproval = (id: string, status: ApprovalStatus = 'approved') => {
  const approvals = listApprovals();
  const updated = approvals
    .map((item) => item.id === id ? { ...item, status } : item)
    .filter((item) => item.id !== id); // remove once processed
  saveApprovals(updated);
  return updated;
};
