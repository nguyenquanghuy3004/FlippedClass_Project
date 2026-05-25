import { clsx } from "clsx";
import { twMerge } from "tailwind-merge";

/**
 * Utility function to merge tailwind classes
 */
export function cn(...inputs) {
  return twMerge(clsx(inputs));
}

/**
 * Helper to get user role color
 */
export const getRoleColor = (role) => {
  switch (role) {
    case 'Admin': return 'bg-purple-100 text-purple-700 border-purple-200';
    case 'Instructor': return 'bg-blue-100 text-blue-700 border-blue-200';
    case 'Student': return 'bg-emerald-100 text-emerald-700 border-emerald-200';
    case 'Facilitator': return 'bg-amber-100 text-amber-700 border-amber-200';
    default: return 'bg-slate-100 text-slate-700 border-slate-200';
  }
};
