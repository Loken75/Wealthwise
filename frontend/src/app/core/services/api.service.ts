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
