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
