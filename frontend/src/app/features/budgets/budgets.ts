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
