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
