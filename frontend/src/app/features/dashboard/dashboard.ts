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
