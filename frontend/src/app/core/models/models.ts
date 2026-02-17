export interface CreateAccountRequest {
  name: string;
  type: AccountType;
  currency: string;
}

export interface AccountResponse {
  id: string;
  name: string;
  type: AccountType;
  currency: string;
  balance: number;
  closed: boolean;
  createdAt: string;
}

export type AccountType = 'CHECKING' | 'SAVINGS' | 'CREDIT_CARD' | 'CASH';

export interface CreateTransactionRequest {
  accountId: string;
  amount: number;
  currency: string;
  description: string;
  date: string;
  type: TransactionType;
}

export interface CategorizeTransactionRequest {
  categoryId: string;
  confidenceLevel: ConfidenceLevel;
}

export interface TransactionResponse {
  id: string;
  accountId: string;
  amount: number;
  currency: string;
  description: string;
  date: string;
  type: TransactionType;
  categoryId: string | null;
  confidenceLevel: ConfidenceLevel | null;
  createdAt: string;
}

export type TransactionType = 'INCOME' | 'EXPENSE' | 'TRANSFER';
export type ConfidenceLevel = 'HIGH' | 'MEDIUM' | 'LOW' | 'MANUAL';

export interface CreateCategoryRequest {
  name: string;
  type: CategoryType;
  color: string;
  icon?: string;
}

export interface CategoryResponse {
  id: string;
  name: string;
  type: CategoryType;
  color: string;
  icon: string | null;
  createdAt: string;
}

export type CategoryType = 'INCOME' | 'EXPENSE';

export interface CreateBudgetRequest {
  categoryId: string;
  limitAmount: number;
  currency: string;
  periodMonth: string;
}

export interface BudgetResponse {
  id: string;
  categoryId: string;
  limitAmount: number;
  spent: number;
  currency: string;
  periodMonth: string;
  status: BudgetStatus;
  createdAt: string;
}

export type BudgetStatus = 'ON_TRACK' | 'WARNING' | 'EXCEEDED';
