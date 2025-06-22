import React, { useState, useEffect } from 'react';
import { Select, Spin, notification } from 'antd';

const { Option } = Select;

const ProductSearchSelect = ({ onSelect, currentSelectedIds }) => {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const BACKEND_URL = 'http://localhost:8080';

  useEffect(() => {
    fetchProducts();
  }, []);

  const fetchProducts = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await fetch(`${BACKEND_URL}/products`);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      const data = await response.json();
      setProducts(data);
    } catch (e) {
      console.error("Erro ao buscar produtos:", e);
      setError("Falha ao carregar produtos. Verifique se o backend está rodando e o endpoint /products existe.");
      notification.error({
        message: 'Erro de Conexão',
        description: 'Não foi possível carregar os produtos do backend.',
        placement: 'bottomRight',
      });
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (value) => {
    const selectedProduct = products.find(p => p.id === value);
    if (selectedProduct) {
      onSelect(selectedProduct);
    }
  };

  const availableProducts = products.filter(
    product => !currentSelectedIds.includes(product.id)
  );

  return (
    <Select
      showSearch
      placeholder="Selecione um produto para adicionar ao pedido"
      optionFilterProp="children"
      onChange={handleChange}
      loading={loading}
      notFoundContent={loading ? <Spin size="small" /> : error || "Nenhum produto encontrado"}
      filterOption={(input, option) =>
        (option?.children || '').toLowerCase().includes(input.toLowerCase())
      }
      style={{ width: '100%' }}
      disabled={loading || availableProducts.length === 0}
    >
      {availableProducts.map((product) => (
        <Option key={product.id} value={product.id}>
          {product.name} (R$ {product.price ? product.price.toFixed(2) : 'N/A'})
        </Option>
      ))}
    </Select>
  );
};

export default ProductSearchSelect;
