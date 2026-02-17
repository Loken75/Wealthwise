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
