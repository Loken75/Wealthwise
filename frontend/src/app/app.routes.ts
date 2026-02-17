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
