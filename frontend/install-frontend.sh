#!/bin/bash
# ===================================================================
# WealthWise Frontend - Script d'installation
# Exécuter depuis ~/projects/dev/java/wealthwise/frontend
# ===================================================================

set -e
cd "$(dirname "$0")"

echo "🚀 Installation du frontend WealthWise..."

# ===== Proxy config =====
cat > proxy.conf.json << 'ENDFILE'
{
  "/api/*": {
    "target": "http://localhost:8080",
    "secure": false,
    "changeOrigin": true
  }
}
ENDFILE
echo "✅ proxy.conf.json"

# ===== Structure =====
mkdir -p src/app/core/{models,services}
mkdir -p src/app/features/{dashboard,accounts,transactions,categories,budgets}
mkdir -p src/app/shared/{components/sidebar,styles}

# ===== Models =====
cat > src/app/core/models/models.ts << 'ENDFILE'
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
ENDFILE
echo "✅ models.ts"

# ===== API Service =====
cat > src/app/core/services/api.service.ts << 'ENDFILE'
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  AccountResponse, CreateAccountRequest,
  TransactionResponse, CreateTransactionRequest, CategorizeTransactionRequest,
  CategoryResponse, CreateCategoryRequest,
  BudgetResponse, CreateBudgetRequest
} from '../models/models';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly baseUrl = '/api';
  constructor(private http: HttpClient) {}

  getAccounts(): Observable<AccountResponse[]> {
    return this.http.get<AccountResponse[]>(`${this.baseUrl}/accounts`);
  }
  getAccount(id: string): Observable<AccountResponse> {
    return this.http.get<AccountResponse>(`${this.baseUrl}/accounts/${id}`);
  }
  createAccount(req: CreateAccountRequest): Observable<AccountResponse> {
    return this.http.post<AccountResponse>(`${this.baseUrl}/accounts`, req);
  }

  getTransactions(): Observable<TransactionResponse[]> {
    return this.http.get<TransactionResponse[]>(`${this.baseUrl}/transactions`);
  }
  createTransaction(req: CreateTransactionRequest): Observable<TransactionResponse> {
    return this.http.post<TransactionResponse>(`${this.baseUrl}/transactions`, req);
  }
  categorizeTransaction(id: string, req: CategorizeTransactionRequest): Observable<TransactionResponse> {
    return this.http.put<TransactionResponse>(`${this.baseUrl}/transactions/${id}/categorize`, req);
  }

  getCategories(): Observable<CategoryResponse[]> {
    return this.http.get<CategoryResponse[]>(`${this.baseUrl}/categories`);
  }
  createCategory(req: CreateCategoryRequest): Observable<CategoryResponse> {
    return this.http.post<CategoryResponse>(`${this.baseUrl}/categories`, req);
  }

  getBudgets(): Observable<BudgetResponse[]> {
    return this.http.get<BudgetResponse[]>(`${this.baseUrl}/budgets`);
  }
  createBudget(req: CreateBudgetRequest): Observable<BudgetResponse> {
    return this.http.post<BudgetResponse>(`${this.baseUrl}/budgets`, req);
  }
}
ENDFILE
echo "✅ api.service.ts"

# ===== App Root =====
cat > src/app/app.ts << 'ENDFILE'
import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { SidebarComponent } from './shared/components/sidebar/sidebar';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, SidebarComponent],
  template: `
    <div class="app-layout">
      <app-sidebar />
      <main class="app-content">
        <router-outlet />
      </main>
    </div>
  `,
  styles: [`
    .app-layout {
      display: flex;
      min-height: 100vh;
    }
    .app-content {
      flex: 1;
      margin-left: 260px;
      padding: 32px 40px;
      max-width: 1200px;
    }
  `]
})
export class AppComponent {}
ENDFILE
echo "✅ app.ts"

cat > src/app/app.config.ts << 'ENDFILE'
import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { routes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    provideHttpClient()
  ]
};
ENDFILE
echo "✅ app.config.ts"

cat > src/app/app.routes.ts << 'ENDFILE'
import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  {
    path: 'dashboard',
    loadComponent: () => import('./features/dashboard/dashboard').then(m => m.DashboardComponent)
  },
  {
    path: 'accounts',
    loadComponent: () => import('./features/accounts/accounts').then(m => m.AccountsComponent)
  },
  {
    path: 'transactions',
    loadComponent: () => import('./features/transactions/transactions').then(m => m.TransactionsComponent)
  },
  {
    path: 'categories',
    loadComponent: () => import('./features/categories/categories').then(m => m.CategoriesComponent)
  },
  {
    path: 'budgets',
    loadComponent: () => import('./features/budgets/budgets').then(m => m.BudgetsComponent)
  },
  { path: '**', redirectTo: 'dashboard' }
];
ENDFILE
echo "✅ app.routes.ts"

# ===== Sidebar =====
cat > src/app/shared/components/sidebar/sidebar.ts << 'ENDFILE'
import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.scss'
})
export class SidebarComponent {}
ENDFILE

cat > src/app/shared/components/sidebar/sidebar.html << 'ENDFILE'
<aside class="sidebar">
  <div class="sidebar-brand">
    <div class="brand-icon">W</div>
    <span class="brand-name">WealthWise</span>
  </div>
  <nav class="sidebar-nav">
    <a routerLink="/dashboard" routerLinkActive="active" class="nav-item">
      <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="7" height="7" rx="1"/><rect x="14" y="3" width="7" height="7" rx="1"/><rect x="3" y="14" width="7" height="7" rx="1"/><rect x="14" y="14" width="7" height="7" rx="1"/></svg>
      <span>Tableau de bord</span>
    </a>
    <a routerLink="/accounts" routerLinkActive="active" class="nav-item">
      <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="5" width="20" height="14" rx="2"/><line x1="2" y1="10" x2="22" y2="10"/></svg>
      <span>Comptes</span>
    </a>
    <a routerLink="/transactions" routerLinkActive="active" class="nav-item">
      <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="22 7 13.5 15.5 8.5 10.5 2 17"/><polyline points="16 7 22 7 22 13"/></svg>
      <span>Transactions</span>
    </a>
    <a routerLink="/categories" routerLinkActive="active" class="nav-item">
      <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="3"/><path d="M12 1v4M12 19v4M4.22 4.22l2.83 2.83M16.95 16.95l2.83 2.83M1 12h4M19 12h4M4.22 19.78l2.83-2.83M16.95 7.05l2.83-2.83"/></svg>
      <span>Catégories</span>
    </a>
    <a routerLink="/budgets" routerLinkActive="active" class="nav-item">
      <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 2v20M17 5H9.5a3.5 3.5 0 000 7h5a3.5 3.5 0 010 7H6"/></svg>
      <span>Budgets</span>
    </a>
  </nav>
  <div class="sidebar-footer">
    <div class="version">v0.1.0</div>
  </div>
</aside>
ENDFILE

cat > src/app/shared/components/sidebar/sidebar.scss << 'ENDFILE'
.sidebar {
  width: 260px;
  height: 100vh;
  background: var(--sidebar-bg);
  color: var(--sidebar-text);
  display: flex;
  flex-direction: column;
  position: fixed;
  left: 0;
  top: 0;
  z-index: 100;
  border-right: 1px solid var(--sidebar-border);
}

.sidebar-brand {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 28px 24px 24px;

  .brand-icon {
    width: 38px;
    height: 38px;
    background: var(--accent);
    color: white;
    border-radius: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: 700;
    font-size: 18px;
    font-family: var(--font-display);
  }

  .brand-name {
    font-family: var(--font-display);
    font-size: 20px;
    font-weight: 700;
    color: var(--sidebar-text-bright);
  }
}

.sidebar-nav {
  flex: 1;
  padding: 8px 12px;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 11px 16px;
  border-radius: 10px;
  text-decoration: none;
  color: var(--sidebar-text);
  font-size: 14px;
  font-weight: 500;
  transition: all 0.15s ease;
  position: relative;

  &:hover {
    background: var(--sidebar-hover);
    color: var(--sidebar-text-bright);
  }

  &.active {
    background: var(--sidebar-active);
    color: var(--accent);
    .nav-icon { color: var(--accent); }
    &::before {
      content: '';
      position: absolute;
      left: -12px;
      top: 50%;
      transform: translateY(-50%);
      width: 3px;
      height: 20px;
      background: var(--accent);
      border-radius: 0 3px 3px 0;
    }
  }
}

.nav-icon {
  width: 20px;
  height: 20px;
  flex-shrink: 0;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.sidebar-footer {
  padding: 16px 24px;
  border-top: 1px solid var(--sidebar-border);
  .version { font-size: 12px; color: var(--sidebar-text-muted); }
}
ENDFILE
echo "✅ sidebar"

# ===== Shared page-common styles =====
cat > src/app/shared/styles/_page-common.scss << 'ENDFILE'
.page { animation: fadeIn 0.3s ease; }

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 28px;

  .page-title {
    font-family: var(--font-display);
    font-size: 28px;
    font-weight: 700;
    color: var(--text-primary);
    letter-spacing: -0.5px;
  }
  .page-subtitle {
    color: var(--text-muted);
    font-size: 14px;
    margin-top: 4px;
  }
}

.form-card {
  background: var(--card-bg);
  border: 1px solid var(--card-border);
  border-radius: 16px;
  padding: 28px;
  margin-bottom: 24px;
  animation: slideDown 0.2s ease;

  h3 {
    font-family: var(--font-display);
    font-size: 18px;
    font-weight: 600;
    color: var(--text-primary);
    margin-bottom: 20px;
  }
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 16px;
  margin-bottom: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;

  label {
    font-size: 13px;
    font-weight: 600;
    color: var(--text-muted);
    text-transform: uppercase;
    letter-spacing: 0.3px;
  }
}

.form-input {
  padding: 10px 14px;
  border: 1px solid var(--card-border);
  border-radius: 10px;
  font-size: 14px;
  background: var(--input-bg);
  color: var(--text-primary);
  transition: border-color 0.15s, box-shadow 0.15s;
  font-family: inherit;

  &:focus {
    outline: none;
    border-color: var(--accent);
    box-shadow: 0 0 0 3px var(--accent-ring);
  }
  &::placeholder { color: var(--text-muted); }
}

select.form-input { cursor: pointer; }

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding-top: 8px;
  border-top: 1px solid var(--card-border);
}

.empty-state, .empty-state-full {
  text-align: center;
  padding: 48px 24px;
  color: var(--text-muted);

  .empty-icon { font-size: 48px; margin-bottom: 16px; }
  h3 {
    font-family: var(--font-display);
    font-size: 18px;
    font-weight: 600;
    color: var(--text-primary);
    margin-bottom: 8px;
  }
  p {
    font-size: 14px;
    margin-bottom: 20px;
    max-width: 360px;
    margin-left: auto;
    margin-right: auto;
  }
}

.empty-state-full {
  background: var(--card-bg);
  border: 1px solid var(--card-border);
  border-radius: 16px;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(8px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes slideDown {
  from { opacity: 0; transform: translateY(-8px); }
  to { opacity: 1; transform: translateY(0); }
}
ENDFILE
echo "✅ shared styles"

# ===== Global styles =====
cat > src/styles.scss << 'ENDFILE'
@import url('https://fonts.googleapis.com/css2?family=DM+Sans:ital,opsz,wght@0,9..40,300;0,9..40,400;0,9..40,500;0,9..40,600;0,9..40,700&family=JetBrains+Mono:wght@400;500;600;700&display=swap');

:root {
  --font-display: 'DM Sans', system-ui, sans-serif;
  --font-body: 'DM Sans', system-ui, sans-serif;
  --font-mono: 'JetBrains Mono', 'Fira Code', monospace;

  --accent: #10B981;
  --accent-hover: #059669;
  --accent-bg: rgba(16, 185, 129, 0.08);
  --accent-ring: rgba(16, 185, 129, 0.2);

  --color-success: #10B981;
  --color-warning: #F59E0B;
  --color-danger: #EF4444;
  --color-info: #3B82F6;

  --bg-app: #F8F9FB;
  --bg-subtle: #F1F3F5;
  --bg-hover: #F4F6F8;
  --input-bg: #FFFFFF;

  --text-primary: #111827;
  --text-secondary: #374151;
  --text-muted: #9CA3AF;

  --card-bg: #FFFFFF;
  --card-border: #E5E7EB;

  --sidebar-bg: #0F172A;
  --sidebar-border: #1E293B;
  --sidebar-text: #94A3B8;
  --sidebar-text-bright: #F1F5F9;
  --sidebar-text-muted: #475569;
  --sidebar-hover: #1E293B;
  --sidebar-active: rgba(16, 185, 129, 0.1);

  --shadow-sm: 0 1px 2px rgba(0, 0, 0, 0.05);
  --shadow-md: 0 4px 12px rgba(0, 0, 0, 0.08);
  --shadow-lg: 0 12px 32px rgba(0, 0, 0, 0.12);
}

*, *::before, *::after { margin: 0; padding: 0; box-sizing: border-box; }

html {
  font-size: 16px;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

body {
  font-family: var(--font-body);
  background: var(--bg-app);
  color: var(--text-primary);
  line-height: 1.5;
}

.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 10px 20px;
  border: none;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 600;
  font-family: var(--font-body);
  cursor: pointer;
  transition: all 0.15s ease;
  text-decoration: none;
  white-space: nowrap;
  &:disabled { opacity: 0.5; cursor: not-allowed; }
}

.btn-primary {
  background: var(--accent);
  color: white;
  &:hover:not(:disabled) {
    background: var(--accent-hover);
    transform: translateY(-1px);
    box-shadow: var(--shadow-sm);
  }
}

.btn-secondary {
  background: var(--bg-subtle);
  color: var(--text-secondary);
  &:hover:not(:disabled) { background: var(--card-border); }
}

.btn-sm {
  padding: 7px 14px;
  font-size: 13px;
  border-radius: 8px;
  background: var(--accent);
  color: white;
  &:hover { background: var(--accent-hover); }
}

::-webkit-scrollbar { width: 6px; }
::-webkit-scrollbar-track { background: transparent; }
::-webkit-scrollbar-thumb {
  background: var(--card-border);
  border-radius: 3px;
  &:hover { background: var(--text-muted); }
}
ENDFILE
echo "✅ styles.scss"

# ===== Dashboard =====
cat > src/app/features/dashboard/dashboard.ts << 'ENDFILE'
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../core/services/api.service';
import { AccountResponse, TransactionResponse, BudgetResponse } from '../../core/models/models';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss'
})
export class DashboardComponent implements OnInit {
  accounts: AccountResponse[] = [];
  transactions: TransactionResponse[] = [];
  budgets: BudgetResponse[] = [];
  totalBalance = 0;
  totalIncome = 0;
  totalExpenses = 0;
  Math = Math;

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.api.getAccounts().subscribe(accounts => {
      this.accounts = accounts;
      this.totalBalance = accounts.reduce((sum, a) => sum + a.balance, 0);
    });
    this.api.getTransactions().subscribe(transactions => {
      this.transactions = transactions.slice(0, 5);
      this.totalIncome = transactions.filter(t => t.type === 'INCOME').reduce((sum, t) => sum + t.amount, 0);
      this.totalExpenses = transactions.filter(t => t.type === 'EXPENSE').reduce((sum, t) => sum + t.amount, 0);
    });
    this.api.getBudgets().subscribe(budgets => this.budgets = budgets);
  }

  formatCurrency(amount: number): string {
    return new Intl.NumberFormat('fr-FR', { style: 'currency', currency: 'EUR' }).format(amount);
  }

  getAccountIcon(type: string): string {
    const icons: Record<string, string> = { 'CHECKING': '🏦', 'SAVINGS': '🐖', 'CREDIT_CARD': '💳', 'CASH': '💵' };
    return icons[type] || '💰';
  }

  getAccountTypeLabel(type: string): string {
    const labels: Record<string, string> = { 'CHECKING': 'Compte courant', 'SAVINGS': 'Épargne', 'CREDIT_CARD': 'Carte de crédit', 'CASH': 'Espèces' };
    return labels[type] || type;
  }
}
ENDFILE

cat > src/app/features/dashboard/dashboard.html << 'ENDFILE'
<div class="dashboard">
  <header class="page-header">
    <div>
      <h1 class="page-title">Tableau de bord</h1>
      <p class="page-subtitle">Vue d'ensemble de vos finances</p>
    </div>
  </header>

  <div class="summary-cards">
    <div class="summary-card card-balance">
      <div class="card-label">Solde total</div>
      <div class="card-value">{{ formatCurrency(totalBalance) }}</div>
      <div class="card-meta">{{ accounts.length }} compte{{ accounts.length > 1 ? 's' : '' }}</div>
    </div>
    <div class="summary-card card-income">
      <div class="card-label">Revenus</div>
      <div class="card-value positive">+{{ formatCurrency(totalIncome) }}</div>
      <div class="card-meta">Ce mois</div>
    </div>
    <div class="summary-card card-expenses">
      <div class="card-label">Dépenses</div>
      <div class="card-value negative">-{{ formatCurrency(totalExpenses) }}</div>
      <div class="card-meta">Ce mois</div>
    </div>
  </div>

  <div class="dashboard-grid">
    <section class="card">
      <div class="card-header">
        <h2>Comptes</h2>
        <a routerLink="/accounts" class="card-link">Voir tout →</a>
      </div>
      <div class="card-body">
        @if (accounts.length === 0) {
          <div class="empty-state">
            <p>Aucun compte</p>
            <a routerLink="/accounts" class="btn btn-sm">Créer un compte</a>
          </div>
        } @else {
          <div class="account-list">
            @for (account of accounts; track account.id) {
              <div class="account-row">
                <div class="account-icon">{{ getAccountIcon(account.type) }}</div>
                <div class="account-info">
                  <div class="account-name">{{ account.name }}</div>
                  <div class="account-type">{{ getAccountTypeLabel(account.type) }}</div>
                </div>
                <div class="account-balance" [class.negative]="account.balance < 0">
                  {{ formatCurrency(account.balance) }}
                </div>
              </div>
            }
          </div>
        }
      </div>
    </section>

    <section class="card">
      <div class="card-header">
        <h2>Dernières transactions</h2>
        <a routerLink="/transactions" class="card-link">Voir tout →</a>
      </div>
      <div class="card-body">
        @if (transactions.length === 0) {
          <div class="empty-state">
            <p>Aucune transaction</p>
            <a routerLink="/transactions" class="btn btn-sm">Ajouter</a>
          </div>
        } @else {
          <div class="transaction-list">
            @for (tx of transactions; track tx.id) {
              <div class="transaction-row">
                <div class="tx-indicator" [class.income]="tx.type === 'INCOME'" [class.expense]="tx.type === 'EXPENSE'"></div>
                <div class="tx-info">
                  <div class="tx-description">{{ tx.description }}</div>
                  <div class="tx-date">{{ tx.date }}</div>
                </div>
                <div class="tx-amount" [class.positive]="tx.type === 'INCOME'" [class.negative]="tx.type === 'EXPENSE'">
                  {{ tx.type === 'INCOME' ? '+' : '-' }}{{ formatCurrency(tx.amount) }}
                </div>
              </div>
            }
          </div>
        }
      </div>
    </section>

    <section class="card">
      <div class="card-header">
        <h2>Budgets</h2>
        <a routerLink="/budgets" class="card-link">Voir tout →</a>
      </div>
      <div class="card-body">
        @if (budgets.length === 0) {
          <div class="empty-state">
            <p>Aucun budget configuré</p>
            <a routerLink="/budgets" class="btn btn-sm">Créer un budget</a>
          </div>
        } @else {
          <div class="budget-list">
            @for (budget of budgets; track budget.id) {
              <div class="budget-row">
                <div class="budget-info">
                  <div class="budget-period">{{ budget.periodMonth }}</div>
                  <div class="budget-progress-bar">
                    <div class="budget-progress-fill"
                         [class.warning]="budget.status === 'WARNING'"
                         [class.exceeded]="budget.status === 'EXCEEDED'"
                         [style.width.%]="Math.min((budget.spent / budget.limitAmount) * 100, 100)">
                    </div>
                  </div>
                </div>
                <div class="budget-amounts">
                  {{ formatCurrency(budget.spent) }} / {{ formatCurrency(budget.limitAmount) }}
                </div>
              </div>
            }
          </div>
        }
      </div>
    </section>
  </div>
</div>
ENDFILE

cat > src/app/features/dashboard/dashboard.scss << 'ENDFILE'
.dashboard { animation: fadeIn 0.3s ease; }

.page-header {
  margin-bottom: 32px;
  .page-title { font-family: var(--font-display); font-size: 28px; font-weight: 700; color: var(--text-primary); letter-spacing: -0.5px; }
  .page-subtitle { color: var(--text-muted); font-size: 14px; margin-top: 4px; }
}

.summary-cards { display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; margin-bottom: 32px; }

.summary-card {
  background: var(--card-bg); border: 1px solid var(--card-border); border-radius: 16px; padding: 24px;
  transition: transform 0.15s ease, box-shadow 0.15s ease;
  &:hover { transform: translateY(-2px); box-shadow: var(--shadow-md); }
  .card-label { font-size: 13px; font-weight: 500; color: var(--text-muted); text-transform: uppercase; letter-spacing: 0.5px; margin-bottom: 8px; }
  .card-value { font-family: var(--font-display); font-size: 28px; font-weight: 700; color: var(--text-primary); letter-spacing: -0.5px;
    &.positive { color: var(--color-success); }
    &.negative { color: var(--color-danger); }
  }
  .card-meta { font-size: 12px; color: var(--text-muted); margin-top: 6px; }
}

.card-balance { border-top: 3px solid var(--accent); }
.card-income { border-top: 3px solid var(--color-success); }
.card-expenses { border-top: 3px solid var(--color-danger); }

.dashboard-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 20px;
  .card:last-child { grid-column: 1 / -1; }
}

.card { background: var(--card-bg); border: 1px solid var(--card-border); border-radius: 16px; overflow: hidden; }
.card-header {
  display: flex; justify-content: space-between; align-items: center; padding: 20px 24px 0;
  h2 { font-family: var(--font-display); font-size: 16px; font-weight: 600; color: var(--text-primary); }
  .card-link { font-size: 13px; color: var(--accent); text-decoration: none; font-weight: 500; &:hover { text-decoration: underline; } }
}
.card-body { padding: 16px 24px 24px; }

.empty-state { text-align: center; padding: 24px 0; color: var(--text-muted); font-size: 14px; .btn { margin-top: 12px; } }

.account-list { display: flex; flex-direction: column; gap: 12px; }
.account-row {
  display: flex; align-items: center; gap: 12px; padding: 12px; border-radius: 10px; transition: background 0.1s;
  &:hover { background: var(--bg-hover); }
  .account-icon { font-size: 24px; width: 40px; height: 40px; display: flex; align-items: center; justify-content: center; background: var(--bg-subtle); border-radius: 10px; }
  .account-info { flex: 1;
    .account-name { font-weight: 600; font-size: 14px; color: var(--text-primary); }
    .account-type { font-size: 12px; color: var(--text-muted); margin-top: 2px; }
  }
  .account-balance { font-family: var(--font-mono); font-weight: 600; font-size: 15px; color: var(--text-primary);
    &.negative { color: var(--color-danger); }
  }
}

.transaction-list { display: flex; flex-direction: column; gap: 8px; }
.transaction-row {
  display: flex; align-items: center; gap: 12px; padding: 10px 12px; border-radius: 10px; transition: background 0.1s;
  &:hover { background: var(--bg-hover); }
  .tx-indicator { width: 8px; height: 8px; border-radius: 50%; flex-shrink: 0;
    &.income { background: var(--color-success); }
    &.expense { background: var(--color-danger); }
  }
  .tx-info { flex: 1;
    .tx-description { font-size: 14px; font-weight: 500; color: var(--text-primary); }
    .tx-date { font-size: 12px; color: var(--text-muted); margin-top: 2px; }
  }
  .tx-amount { font-family: var(--font-mono); font-weight: 600; font-size: 14px;
    &.positive { color: var(--color-success); }
    &.negative { color: var(--color-danger); }
  }
}

.budget-list { display: flex; flex-direction: column; gap: 16px; }
.budget-row {
  display: flex; align-items: center; gap: 16px;
  .budget-info { flex: 1;
    .budget-period { font-size: 13px; font-weight: 500; color: var(--text-primary); margin-bottom: 6px; }
  }
  .budget-progress-bar { height: 6px; background: var(--bg-subtle); border-radius: 3px; overflow: hidden;
    .budget-progress-fill { height: 100%; background: var(--accent); border-radius: 3px; transition: width 0.3s ease;
      &.warning { background: var(--color-warning); }
      &.exceeded { background: var(--color-danger); }
    }
  }
  .budget-amounts { font-size: 13px; font-family: var(--font-mono); color: var(--text-muted); white-space: nowrap; }
}

@keyframes fadeIn { from { opacity: 0; transform: translateY(8px); } to { opacity: 1; transform: translateY(0); } }
ENDFILE
echo "✅ dashboard"

# ===== Accounts =====
cat > src/app/features/accounts/accounts.ts << 'ENDFILE'
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { AccountResponse, AccountType, CreateAccountRequest } from '../../core/models/models';

@Component({
  selector: 'app-accounts',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './accounts.html',
  styleUrl: './accounts.scss'
})
export class AccountsComponent implements OnInit {
  accounts: AccountResponse[] = [];
  showForm = false;
  newAccount: CreateAccountRequest = { name: '', type: 'CHECKING', currency: 'EUR' };

  accountTypes: { value: AccountType; label: string; icon: string }[] = [
    { value: 'CHECKING', label: 'Compte courant', icon: '🏦' },
    { value: 'SAVINGS', label: 'Épargne', icon: '🐖' },
    { value: 'CREDIT_CARD', label: 'Carte de crédit', icon: '💳' },
    { value: 'CASH', label: 'Espèces', icon: '💵' }
  ];

  constructor(private api: ApiService) {}
  ngOnInit(): void { this.loadAccounts(); }

  loadAccounts(): void { this.api.getAccounts().subscribe(a => this.accounts = a); }

  toggleForm(): void { this.showForm = !this.showForm; if (!this.showForm) this.resetForm(); }

  createAccount(): void {
    if (!this.newAccount.name.trim()) return;
    this.api.createAccount(this.newAccount).subscribe(() => { this.loadAccounts(); this.toggleForm(); });
  }

  resetForm(): void { this.newAccount = { name: '', type: 'CHECKING', currency: 'EUR' }; }
  formatCurrency(amount: number): string { return new Intl.NumberFormat('fr-FR', { style: 'currency', currency: 'EUR' }).format(amount); }
  getTypeIcon(type: string): string { return this.accountTypes.find(t => t.value === type)?.icon || '💰'; }
  getTypeLabel(type: string): string { return this.accountTypes.find(t => t.value === type)?.label || type; }
  getTotalBalance(): number { return this.accounts.reduce((sum, a) => sum + a.balance, 0); }
}
ENDFILE

cat > src/app/features/accounts/accounts.html << 'ENDFILE'
<div class="page">
  <header class="page-header">
    <div>
      <h1 class="page-title">Comptes</h1>
      <p class="page-subtitle">Gérez vos comptes bancaires</p>
    </div>
    <button class="btn btn-primary" (click)="toggleForm()">{{ showForm ? '✕ Annuler' : '+ Nouveau compte' }}</button>
  </header>

  @if (showForm) {
    <div class="form-card">
      <h3>Nouveau compte</h3>
      <div class="form-grid">
        <div class="form-group">
          <label for="name">Nom du compte</label>
          <input id="name" type="text" [(ngModel)]="newAccount.name" placeholder="ex: Compte Courant BNP" class="form-input" autofocus>
        </div>
        <div class="form-group">
          <label>Type de compte</label>
          <div class="type-selector">
            @for (t of accountTypes; track t.value) {
              <button class="type-option" [class.selected]="newAccount.type === t.value" (click)="newAccount.type = t.value">
                <span class="type-icon">{{ t.icon }}</span>
                <span class="type-label">{{ t.label }}</span>
              </button>
            }
          </div>
        </div>
        <div class="form-group">
          <label for="currency">Devise</label>
          <select id="currency" [(ngModel)]="newAccount.currency" class="form-input">
            <option value="EUR">EUR — Euro</option>
            <option value="USD">USD — Dollar</option>
            <option value="GBP">GBP — Livre</option>
          </select>
        </div>
      </div>
      <div class="form-actions">
        <button class="btn btn-secondary" (click)="toggleForm()">Annuler</button>
        <button class="btn btn-primary" (click)="createAccount()" [disabled]="!newAccount.name.trim()">Créer le compte</button>
      </div>
    </div>
  }

  @if (accounts.length > 0) {
    <div class="total-bar">
      <span>Solde total</span>
      <span class="total-amount">{{ formatCurrency(getTotalBalance()) }}</span>
    </div>
  }

  <div class="accounts-grid">
    @if (accounts.length === 0) {
      <div class="empty-state">
        <div class="empty-icon">🏦</div>
        <h3>Aucun compte</h3>
        <p>Créez votre premier compte pour commencer.</p>
        <button class="btn btn-primary" (click)="toggleForm()">+ Créer un compte</button>
      </div>
    } @else {
      @for (account of accounts; track account.id) {
        <div class="account-card" [class.closed]="account.closed">
          <div class="account-card-header">
            <div class="account-icon">{{ getTypeIcon(account.type) }}</div>
            @if (account.closed) { <span class="badge badge-closed">Fermé</span> }
          </div>
          <div class="account-card-name">{{ account.name }}</div>
          <div class="account-card-type">{{ getTypeLabel(account.type) }} · {{ account.currency }}</div>
          <div class="account-card-balance" [class.negative]="account.balance < 0">{{ formatCurrency(account.balance) }}</div>
        </div>
      }
    }
  </div>
</div>
ENDFILE

cat > src/app/features/accounts/accounts.scss << 'ENDFILE'
@import '../../shared/styles/page-common';

.total-bar {
  display: flex; justify-content: space-between; align-items: center;
  background: var(--card-bg); border: 1px solid var(--card-border); border-radius: 12px; padding: 16px 24px; margin-bottom: 24px;
  font-size: 14px; color: var(--text-muted);
  .total-amount { font-family: var(--font-display); font-size: 22px; font-weight: 700; color: var(--text-primary); }
}

.accounts-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 16px; }

.account-card {
  background: var(--card-bg); border: 1px solid var(--card-border); border-radius: 16px; padding: 24px;
  transition: transform 0.15s ease, box-shadow 0.15s ease; cursor: default;
  &:hover { transform: translateY(-2px); box-shadow: var(--shadow-md); }
  &.closed { opacity: 0.5; }
  .account-card-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 16px; }
  .account-icon { font-size: 32px; width: 52px; height: 52px; display: flex; align-items: center; justify-content: center; background: var(--bg-subtle); border-radius: 14px; }
  .account-card-name { font-weight: 600; font-size: 16px; color: var(--text-primary); margin-bottom: 4px; }
  .account-card-type { font-size: 13px; color: var(--text-muted); margin-bottom: 16px; }
  .account-card-balance { font-family: var(--font-mono); font-size: 22px; font-weight: 700; color: var(--text-primary); letter-spacing: -0.5px;
    &.negative { color: var(--color-danger); }
  }
}

.badge-closed { background: var(--color-danger); color: white; font-size: 11px; font-weight: 600; padding: 3px 8px; border-radius: 6px; }

.type-selector { display: grid; grid-template-columns: repeat(2, 1fr); gap: 8px; }
.type-option {
  display: flex; align-items: center; gap: 10px; padding: 12px 14px;
  border: 1px solid var(--card-border); border-radius: 10px; background: var(--card-bg);
  cursor: pointer; transition: all 0.15s; font-size: 13px; color: var(--text-primary);
  &:hover { border-color: var(--accent); }
  &.selected { border-color: var(--accent); background: var(--accent-bg); }
  .type-icon { font-size: 20px; }
  .type-label { font-weight: 500; }
}
ENDFILE
echo "✅ accounts"

# ===== Transactions =====
cat > src/app/features/transactions/transactions.ts << 'ENDFILE'
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { TransactionResponse, CreateTransactionRequest, TransactionType, AccountResponse, CategoryResponse } from '../../core/models/models';

@Component({
  selector: 'app-transactions',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './transactions.html',
  styleUrl: './transactions.scss'
})
export class TransactionsComponent implements OnInit {
  transactions: TransactionResponse[] = [];
  accounts: AccountResponse[] = [];
  categories: CategoryResponse[] = [];
  showForm = false;
  newTx: CreateTransactionRequest = { accountId: '', amount: 0, currency: 'EUR', description: '', date: new Date().toISOString().split('T')[0], type: 'EXPENSE' };

  constructor(private api: ApiService) {}
  ngOnInit(): void { this.loadAll(); }

  loadAll(): void {
    this.api.getTransactions().subscribe(tx => this.transactions = tx.sort((a, b) => b.date.localeCompare(a.date)));
    this.api.getAccounts().subscribe(a => this.accounts = a);
    this.api.getCategories().subscribe(c => this.categories = c);
  }

  toggleForm(): void { this.showForm = !this.showForm; if (!this.showForm) this.resetForm(); }

  createTransaction(): void {
    if (!this.newTx.accountId || !this.newTx.amount || !this.newTx.description.trim()) return;
    const account = this.accounts.find(a => a.id === this.newTx.accountId);
    if (account) this.newTx.currency = account.currency;
    this.api.createTransaction(this.newTx).subscribe(() => { this.loadAll(); this.toggleForm(); });
  }

  resetForm(): void {
    this.newTx = { accountId: this.accounts.length > 0 ? this.accounts[0].id : '', amount: 0, currency: 'EUR', description: '', date: new Date().toISOString().split('T')[0], type: 'EXPENSE' };
  }

  setType(type: TransactionType): void { this.newTx.type = type; }
  formatCurrency(amount: number): string { return new Intl.NumberFormat('fr-FR', { style: 'currency', currency: 'EUR' }).format(amount); }
  getAccountName(id: string): string { return this.accounts.find(a => a.id === id)?.name || 'Inconnu'; }
  getCategoryName(id: string | null): string { if (!id) return 'Non catégorisée'; return this.categories.find(c => c.id === id)?.name || 'Inconnue'; }
}
ENDFILE

cat > src/app/features/transactions/transactions.html << 'ENDFILE'
<div class="page">
  <header class="page-header">
    <div>
      <h1 class="page-title">Transactions</h1>
      <p class="page-subtitle">{{ transactions.length }} transaction{{ transactions.length > 1 ? 's' : '' }}</p>
    </div>
    <button class="btn btn-primary" (click)="toggleForm()">{{ showForm ? '✕ Annuler' : '+ Nouvelle transaction' }}</button>
  </header>

  @if (showForm) {
    <div class="form-card">
      <h3>Nouvelle transaction</h3>
      <div class="type-toggle">
        <button class="toggle-btn" [class.active]="newTx.type === 'INCOME'" [class.income]="newTx.type === 'INCOME'" (click)="setType('INCOME')">Revenu</button>
        <button class="toggle-btn" [class.active]="newTx.type === 'EXPENSE'" [class.expense]="newTx.type === 'EXPENSE'" (click)="setType('EXPENSE')">Dépense</button>
      </div>
      <div class="form-grid">
        <div class="form-group">
          <label for="account">Compte</label>
          <select id="account" [(ngModel)]="newTx.accountId" class="form-input">
            <option value="" disabled>Sélectionner un compte</option>
            @for (a of accounts; track a.id) { <option [value]="a.id">{{ a.name }}</option> }
          </select>
        </div>
        <div class="form-group">
          <label for="amount">Montant</label>
          <input id="amount" type="number" [(ngModel)]="newTx.amount" placeholder="0.00" class="form-input form-input-amount" min="0.01" step="0.01">
        </div>
        <div class="form-group">
          <label for="description">Description</label>
          <input id="description" type="text" [(ngModel)]="newTx.description" placeholder="ex: Courses Carrefour" class="form-input">
        </div>
        <div class="form-group">
          <label for="date">Date</label>
          <input id="date" type="date" [(ngModel)]="newTx.date" class="form-input">
        </div>
      </div>
      <div class="form-actions">
        <button class="btn btn-secondary" (click)="toggleForm()">Annuler</button>
        <button class="btn btn-primary" (click)="createTransaction()" [disabled]="!newTx.accountId || !newTx.amount || !newTx.description.trim()">Enregistrer</button>
      </div>
    </div>
  }

  <div class="table-card">
    @if (transactions.length === 0) {
      <div class="empty-state">
        <div class="empty-icon">📊</div>
        <h3>Aucune transaction</h3>
        <p>Ajoutez votre première transaction.</p>
        <button class="btn btn-primary" (click)="toggleForm()">+ Ajouter</button>
      </div>
    } @else {
      <table class="data-table">
        <thead><tr><th>Date</th><th>Description</th><th>Compte</th><th>Catégorie</th><th class="text-right">Montant</th></tr></thead>
        <tbody>
          @for (tx of transactions; track tx.id) {
            <tr>
              <td class="cell-date">{{ tx.date }}</td>
              <td class="cell-description">
                <div class="tx-indicator-inline" [class.income]="tx.type === 'INCOME'" [class.expense]="tx.type === 'EXPENSE'"></div>
                {{ tx.description }}
              </td>
              <td class="cell-muted">{{ getAccountName(tx.accountId) }}</td>
              <td class="cell-muted">{{ getCategoryName(tx.categoryId) }}</td>
              <td class="cell-amount" [class.positive]="tx.type === 'INCOME'" [class.negative]="tx.type === 'EXPENSE'">
                {{ tx.type === 'INCOME' ? '+' : '-' }}{{ formatCurrency(tx.amount) }}
              </td>
            </tr>
          }
        </tbody>
      </table>
    }
  </div>
</div>
ENDFILE

cat > src/app/features/transactions/transactions.scss << 'ENDFILE'
@import '../../shared/styles/page-common';

.type-toggle { display: flex; gap: 8px; margin-bottom: 20px; background: var(--bg-subtle); border-radius: 10px; padding: 4px; }
.toggle-btn {
  flex: 1; padding: 10px; border: none; border-radius: 8px; font-size: 14px; font-weight: 600; cursor: pointer; background: transparent; color: var(--text-muted); transition: all 0.15s;
  &.active.income { background: var(--color-success); color: white; }
  &.active.expense { background: var(--color-danger); color: white; }
}
.form-input-amount { font-family: var(--font-mono); font-size: 18px; font-weight: 600; }

.table-card { background: var(--card-bg); border: 1px solid var(--card-border); border-radius: 16px; overflow: hidden; }
.data-table {
  width: 100%; border-collapse: collapse;
  th { text-align: left; padding: 14px 20px; font-size: 12px; font-weight: 600; color: var(--text-muted); text-transform: uppercase; letter-spacing: 0.5px; background: var(--bg-subtle); border-bottom: 1px solid var(--card-border);
    &.text-right { text-align: right; }
  }
  td { padding: 14px 20px; font-size: 14px; border-bottom: 1px solid var(--card-border); color: var(--text-primary); }
  tbody tr { transition: background 0.1s; &:hover { background: var(--bg-hover); } &:last-child td { border-bottom: none; } }
  .cell-date { font-family: var(--font-mono); font-size: 13px; color: var(--text-muted); white-space: nowrap; }
  .cell-description { font-weight: 500; display: flex; align-items: center; gap: 10px; }
  .tx-indicator-inline { width: 8px; height: 8px; border-radius: 50%; flex-shrink: 0;
    &.income { background: var(--color-success); } &.expense { background: var(--color-danger); }
  }
  .cell-muted { color: var(--text-muted); font-size: 13px; }
  .cell-amount { text-align: right; font-family: var(--font-mono); font-weight: 600; white-space: nowrap;
    &.positive { color: var(--color-success); } &.negative { color: var(--color-danger); }
  }
}
ENDFILE
echo "✅ transactions"

# ===== Categories =====
cat > src/app/features/categories/categories.ts << 'ENDFILE'
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { CategoryResponse, CreateCategoryRequest, CategoryType } from '../../core/models/models';

@Component({
  selector: 'app-categories',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './categories.html',
  styleUrl: './categories.scss'
})
export class CategoriesComponent implements OnInit {
  categories: CategoryResponse[] = [];
  showForm = false;
  newCategory: CreateCategoryRequest = { name: '', type: 'EXPENSE', color: '#10B981', icon: '' };
  presetColors = ['#10B981', '#3B82F6', '#8B5CF6', '#F59E0B', '#EF4444', '#EC4899', '#14B8A6', '#F97316'];

  constructor(private api: ApiService) {}
  ngOnInit(): void { this.loadCategories(); }
  loadCategories(): void { this.api.getCategories().subscribe(c => this.categories = c); }
  toggleForm(): void { this.showForm = !this.showForm; if (!this.showForm) this.resetForm(); }

  createCategory(): void {
    if (!this.newCategory.name.trim()) return;
    this.api.createCategory(this.newCategory).subscribe(() => { this.loadCategories(); this.toggleForm(); });
  }

  resetForm(): void { this.newCategory = { name: '', type: 'EXPENSE', color: '#10B981', icon: '' }; }
  setType(type: CategoryType): void { this.newCategory.type = type; }
  setColor(color: string): void { this.newCategory.color = color; }
  getExpenseCategories(): CategoryResponse[] { return this.categories.filter(c => c.type === 'EXPENSE'); }
  getIncomeCategories(): CategoryResponse[] { return this.categories.filter(c => c.type === 'INCOME'); }
}
ENDFILE

cat > src/app/features/categories/categories.html << 'ENDFILE'
<div class="page">
  <header class="page-header">
    <div>
      <h1 class="page-title">Catégories</h1>
      <p class="page-subtitle">Organisez vos revenus et dépenses</p>
    </div>
    <button class="btn btn-primary" (click)="toggleForm()">{{ showForm ? '✕ Annuler' : '+ Nouvelle catégorie' }}</button>
  </header>

  @if (showForm) {
    <div class="form-card">
      <h3>Nouvelle catégorie</h3>
      <div class="type-toggle">
        <button class="toggle-btn" [class.active]="newCategory.type === 'EXPENSE'" (click)="setType('EXPENSE')">Dépense</button>
        <button class="toggle-btn" [class.active]="newCategory.type === 'INCOME'" (click)="setType('INCOME')">Revenu</button>
      </div>
      <div class="form-grid">
        <div class="form-group">
          <label for="name">Nom</label>
          <input id="name" type="text" [(ngModel)]="newCategory.name" placeholder="ex: Alimentation" class="form-input" autofocus>
        </div>
        <div class="form-group">
          <label>Couleur</label>
          <div class="color-picker">
            @for (color of presetColors; track color) {
              <button class="color-swatch" [style.background]="color" [class.selected]="newCategory.color === color" (click)="setColor(color)">
                @if (newCategory.color === color) {
                  <svg viewBox="0 0 24 24" width="14" height="14"><polyline points="20 6 9 17 4 12" fill="none" stroke="white" stroke-width="3"/></svg>
                }
              </button>
            }
          </div>
        </div>
      </div>
      <div class="form-actions">
        <button class="btn btn-secondary" (click)="toggleForm()">Annuler</button>
        <button class="btn btn-primary" (click)="createCategory()" [disabled]="!newCategory.name.trim()">Créer</button>
      </div>
    </div>
  }

  <section class="category-section">
    <h2 class="section-title">Dépenses</h2>
    @if (getExpenseCategories().length === 0) { <p class="section-empty">Aucune catégorie de dépense</p> }
    @else {
      <div class="category-grid">
        @for (cat of getExpenseCategories(); track cat.id) {
          <div class="category-chip"><span class="chip-dot" [style.background]="cat.color"></span><span class="chip-name">{{ cat.name }}</span></div>
        }
      </div>
    }
  </section>

  <section class="category-section">
    <h2 class="section-title">Revenus</h2>
    @if (getIncomeCategories().length === 0) { <p class="section-empty">Aucune catégorie de revenu</p> }
    @else {
      <div class="category-grid">
        @for (cat of getIncomeCategories(); track cat.id) {
          <div class="category-chip"><span class="chip-dot" [style.background]="cat.color"></span><span class="chip-name">{{ cat.name }}</span></div>
        }
      </div>
    }
  </section>
</div>
ENDFILE

cat > src/app/features/categories/categories.scss << 'ENDFILE'
@import '../../shared/styles/page-common';

.type-toggle { display: flex; gap: 8px; margin-bottom: 20px; background: var(--bg-subtle); border-radius: 10px; padding: 4px; }
.toggle-btn { flex: 1; padding: 10px; border: none; border-radius: 8px; font-size: 14px; font-weight: 600; cursor: pointer; background: transparent; color: var(--text-muted); transition: all 0.15s;
  &.active { background: var(--accent); color: white; }
}

.color-picker { display: flex; gap: 8px; flex-wrap: wrap; }
.color-swatch { width: 36px; height: 36px; border-radius: 10px; border: 2px solid transparent; cursor: pointer; transition: all 0.15s; display: flex; align-items: center; justify-content: center;
  &:hover { transform: scale(1.1); }
  &.selected { border-color: var(--text-primary); box-shadow: 0 0 0 2px var(--card-bg), 0 0 0 4px var(--text-primary); }
}

.category-section { margin-bottom: 32px;
  .section-title { font-family: var(--font-display); font-size: 16px; font-weight: 600; color: var(--text-primary); margin-bottom: 16px; }
  .section-empty { font-size: 14px; color: var(--text-muted); }
}

.category-grid { display: flex; flex-wrap: wrap; gap: 10px; }
.category-chip {
  display: flex; align-items: center; gap: 10px; padding: 10px 18px;
  background: var(--card-bg); border: 1px solid var(--card-border); border-radius: 12px;
  font-size: 14px; font-weight: 500; color: var(--text-primary); transition: transform 0.1s;
  &:hover { transform: translateY(-1px); }
  .chip-dot { width: 10px; height: 10px; border-radius: 50%; flex-shrink: 0; }
}
ENDFILE
echo "✅ categories"

# ===== Budgets =====
cat > src/app/features/budgets/budgets.ts << 'ENDFILE'
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { BudgetResponse, CreateBudgetRequest, CategoryResponse } from '../../core/models/models';

@Component({
  selector: 'app-budgets',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './budgets.html',
  styleUrl: './budgets.scss'
})
export class BudgetsComponent implements OnInit {
  budgets: BudgetResponse[] = [];
  categories: CategoryResponse[] = [];
  showForm = false;
  newBudget: CreateBudgetRequest = { categoryId: '', limitAmount: 0, currency: 'EUR', periodMonth: new Date().toISOString().slice(0, 7) };

  constructor(private api: ApiService) {}
  ngOnInit(): void { this.loadAll(); }

  loadAll(): void {
    this.api.getBudgets().subscribe(b => this.budgets = b);
    this.api.getCategories().subscribe(c => this.categories = c.filter(cat => cat.type === 'EXPENSE'));
  }

  toggleForm(): void { this.showForm = !this.showForm; if (!this.showForm) this.resetForm(); }

  createBudget(): void {
    if (!this.newBudget.categoryId || !this.newBudget.limitAmount) return;
    this.api.createBudget(this.newBudget).subscribe(() => { this.loadAll(); this.toggleForm(); });
  }

  resetForm(): void { this.newBudget = { categoryId: '', limitAmount: 0, currency: 'EUR', periodMonth: new Date().toISOString().slice(0, 7) }; }
  formatCurrency(amount: number): string { return new Intl.NumberFormat('fr-FR', { style: 'currency', currency: 'EUR' }).format(amount); }
  getCategoryName(id: string): string { return this.categories.find(c => c.id === id)?.name || 'Inconnue'; }
  getCategoryColor(id: string): string { return this.categories.find(c => c.id === id)?.color || '#6B7280'; }
  getUsagePercent(budget: BudgetResponse): number { if (budget.limitAmount === 0) return 0; return Math.min((budget.spent / budget.limitAmount) * 100, 100); }
  getStatusLabel(status: string): string { const l: Record<string, string> = { 'ON_TRACK': 'En bonne voie', 'WARNING': 'Attention', 'EXCEEDED': 'Dépassé' }; return l[status] || status; }
}
ENDFILE

cat > src/app/features/budgets/budgets.html << 'ENDFILE'
<div class="page">
  <header class="page-header">
    <div>
      <h1 class="page-title">Budgets</h1>
      <p class="page-subtitle">Suivez vos limites de dépenses</p>
    </div>
    <button class="btn btn-primary" (click)="toggleForm()">{{ showForm ? '✕ Annuler' : '+ Nouveau budget' }}</button>
  </header>

  @if (showForm) {
    <div class="form-card">
      <h3>Nouveau budget</h3>
      <div class="form-grid">
        <div class="form-group">
          <label for="category">Catégorie</label>
          <select id="category" [(ngModel)]="newBudget.categoryId" class="form-input">
            <option value="" disabled>Sélectionner une catégorie</option>
            @for (cat of categories; track cat.id) { <option [value]="cat.id">{{ cat.name }}</option> }
          </select>
        </div>
        <div class="form-group">
          <label for="limit">Limite mensuelle (€)</label>
          <input id="limit" type="number" [(ngModel)]="newBudget.limitAmount" placeholder="0.00" class="form-input" min="1" step="10">
        </div>
        <div class="form-group">
          <label for="period">Mois</label>
          <input id="period" type="month" [(ngModel)]="newBudget.periodMonth" class="form-input">
        </div>
      </div>
      <div class="form-actions">
        <button class="btn btn-secondary" (click)="toggleForm()">Annuler</button>
        <button class="btn btn-primary" (click)="createBudget()" [disabled]="!newBudget.categoryId || !newBudget.limitAmount">Créer le budget</button>
      </div>
    </div>
  }

  @if (budgets.length === 0) {
    <div class="empty-state-full">
      <div class="empty-icon">💰</div>
      <h3>Aucun budget</h3>
      <p>Définissez des limites pour mieux contrôler vos finances.</p>
      <button class="btn btn-primary" (click)="toggleForm()">+ Créer un budget</button>
    </div>
  } @else {
    <div class="budget-cards">
      @for (budget of budgets; track budget.id) {
        <div class="budget-card" [class.warning]="budget.status === 'WARNING'" [class.exceeded]="budget.status === 'EXCEEDED'">
          <div class="budget-card-header">
            <div class="budget-category">
              <span class="budget-dot" [style.background]="getCategoryColor(budget.categoryId)"></span>
              {{ getCategoryName(budget.categoryId) }}
            </div>
            <span class="budget-status" [class]="budget.status.toLowerCase()">{{ getStatusLabel(budget.status) }}</span>
          </div>
          <div class="budget-amounts-row">
            <span class="budget-spent">{{ formatCurrency(budget.spent) }}</span>
            <span class="budget-limit">sur {{ formatCurrency(budget.limitAmount) }}</span>
          </div>
          <div class="budget-bar">
            <div class="budget-bar-fill" [class.warning]="budget.status === 'WARNING'" [class.exceeded]="budget.status === 'EXCEEDED'" [style.width.%]="getUsagePercent(budget)"></div>
          </div>
          <div class="budget-footer">
            <span class="budget-period-label">{{ budget.periodMonth }}</span>
            <span class="budget-remaining">Reste : {{ formatCurrency(budget.limitAmount - budget.spent) }}</span>
          </div>
        </div>
      }
    </div>
  }
</div>
ENDFILE

cat > src/app/features/budgets/budgets.scss << 'ENDFILE'
@import '../../shared/styles/page-common';

.budget-cards { display: grid; grid-template-columns: repeat(auto-fill, minmax(340px, 1fr)); gap: 16px; }

.budget-card {
  background: var(--card-bg); border: 1px solid var(--card-border); border-radius: 16px; padding: 24px; transition: transform 0.15s;
  &:hover { transform: translateY(-2px); }
  &.warning { border-left: 3px solid var(--color-warning); }
  &.exceeded { border-left: 3px solid var(--color-danger); }

  .budget-card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
  .budget-category { display: flex; align-items: center; gap: 8px; font-weight: 600; font-size: 15px; color: var(--text-primary); }
  .budget-dot { width: 10px; height: 10px; border-radius: 50%; }
  .budget-status { font-size: 12px; font-weight: 600; padding: 4px 10px; border-radius: 20px;
    &.on_track { background: rgba(16, 185, 129, 0.1); color: var(--color-success); }
    &.warning { background: rgba(245, 158, 11, 0.1); color: var(--color-warning); }
    &.exceeded { background: rgba(239, 68, 68, 0.1); color: var(--color-danger); }
  }
  .budget-amounts-row { margin-bottom: 12px;
    .budget-spent { font-family: var(--font-mono); font-size: 22px; font-weight: 700; color: var(--text-primary); }
    .budget-limit { font-size: 14px; color: var(--text-muted); margin-left: 4px; }
  }
  .budget-bar { height: 8px; background: var(--bg-subtle); border-radius: 4px; overflow: hidden; margin-bottom: 14px;
    .budget-bar-fill { height: 100%; background: var(--accent); border-radius: 4px; transition: width 0.4s ease;
      &.warning { background: var(--color-warning); } &.exceeded { background: var(--color-danger); }
    }
  }
  .budget-footer { display: flex; justify-content: space-between; font-size: 12px; color: var(--text-muted); }
}
ENDFILE
echo "✅ budgets"

# ===== Supprimer les fichiers par défaut =====
rm -f src/app/app.html src/app/app.scss src/app/app.spec.ts

echo ""
echo "🎉 Frontend WealthWise installé avec succès !"
echo ""
echo "Prochaines étapes :"
echo "  1. ng serve --proxy-config proxy.conf.json"
echo "  2. Ouvrir http://localhost:4200"
echo "  3. (Dans un autre terminal) mvn spring-boot:run"
