import apiClient from '@/services/apiClient';
import { unwrap } from '@/services/unwrap';
import { Product, ProductCreate, ProductUpdate } from '@/types/product';

export const getProducts = (keyword?: string) =>
  unwrap<Product[]>(apiClient.get('/products', { params: { keyword: keyword || undefined } }));

export const createProduct = (body: ProductCreate) =>
  unwrap<Product>(apiClient.post('/products', body));

export const updateProduct = (id: number, body: ProductUpdate) =>
  unwrap<Product>(apiClient.put(`/products/${id}`, body));

export const deleteProduct = (id: number) => unwrap<null>(apiClient.delete(`/products/${id}`));
