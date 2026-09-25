import apiClient from '@/services/apiClient';
import { unwrap } from '@/services/unwrap';
import { Product, ProductCreate } from '@/types/product';

export const getProducts = (keyword?: string) =>
  unwrap<Product[]>(apiClient.get('/products', { params: { keyword: keyword || undefined } }));

export const createProduct = (body: ProductCreate) =>
  unwrap<Product>(apiClient.post('/products', body));
