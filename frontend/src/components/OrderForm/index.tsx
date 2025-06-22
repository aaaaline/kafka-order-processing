import React, { useState } from 'react';
import { Form, Input, Button, Card, Space, Typography, InputNumber, Popconfirm, notification } from 'antd';
import { DeleteOutlined } from '@ant-design/icons';
import ProductSearchSelect from '../ProductSearchSelect'

const { Title, Text } = Typography;

const OrderForm = () => {
  const [form] = Form.useForm();
  const [orderItems, setOrderItems] = useState([]);
  const [loading, setLoading] = useState(false);

  const BACKEND_URL = 'http://localhost:8080';

  const handleProductSelect = (product) => {
    if (!orderItems.some(item => item.product.id === product.id)) {
      setOrderItems([...orderItems, { product: product, quantity: 1 }]);
    } else {
      notification.warning({
        message: 'Produto Já Adicionado',
        description: `${product.name} já está na lista de itens do pedido.`,
        placement: 'bottomRight',
      });
    }
  };

  const handleQuantityChange = (productId, newQuantity) => {
    setOrderItems(prevItems =>
      prevItems.map(item =>
        item.product.id === productId ? { ...item, quantity: newQuantity } : item
      )
    );
  };

  const handleRemoveItem = (productId) => {
    setOrderItems(prevItems => prevItems.filter(item => item.product.id !== productId));
  };

  const handleSubmit = async (values) => {
    if (orderItems.length === 0) {
      notification.error({
        message: 'Pedido Vazio',
        description: 'Adicione pelo menos um item ao pedido.',
        placement: 'bottomRight',
      });
      return;
    }

    setLoading(true);
    try {
      const orderRequest = {
        customerName: values.customerName,
        items: orderItems.map(item => ({
          productId: item.product.id,
          quantity: item.quantity
        }))
      };

      const response = await fetch(`${BACKEND_URL}/orders`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(orderRequest),
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const createdOrder = await response.json();
      notification.success({
        message: 'Pedido Registrado!',
        description: `Pedido ${createdOrder.orderId} de ${createdOrder.customerName} enviado ao Kafka e salvo no DB.`,
        placement: 'bottomRight',
      });
      form.resetFields();
      setOrderItems([]);
      console.log('Pedido criado:', createdOrder);

    } catch (e) {
      console.error('Erro ao registrar pedido:', e);
      notification.error({
        message: 'Erro ao Registrar Pedido',
        description: `Falha: ${e.message}. Verifique os logs do backend.`,
        placement: 'bottomRight',
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <Card title="Criar Novo Pedido" style={{ maxWidth: '600px', margin: 'auto' }}>
      <Form
        form={form}
        layout="vertical"
        onFinish={handleSubmit}
      >
        <Form.Item
          label="Nome do Cliente"
          name="customerName"
          rules={[{ required: true, message: 'Por favor, insira o nome do cliente!' }]}
        >
          <Input placeholder="Ex: João Silva" />
        </Form.Item>

        <Form.Item label="Adicionar Produtos">
          <ProductSearchSelect
            onSelect={handleProductSelect}
            currentSelectedIds={orderItems.map(item => item.product.id)}
          />
        </Form.Item>

        {orderItems.length > 0 && (
          <Card title="Itens do Pedido" size="small" style={{ marginBottom: '20px' }}>
            {orderItems.map(item => (
              <Space key={item.product.id} style={{ display: 'flex', marginBottom: 8, justifyContent: 'space-between' }} align="baseline">
                <Text strong>{item.product.name}</Text>
                <Text type="secondary">(R$ {item.product.price ? item.product.price.toFixed(2) : 'N/A'})</Text>
                <InputNumber
                  min={1}
                  value={item.quantity}
                  onChange={(value) => handleQuantityChange(item.product.id, value)}
                  style={{ width: 80 }}
                />
                <Popconfirm
                  title="Remover item?"
                  onConfirm={() => handleRemoveItem(item.product.id)}
                  okText="Sim"
                  cancelText="Não"
                >
                  <Button icon={<DeleteOutlined />} danger size="small" />
                </Popconfirm>
              </Space>
            ))}
            <Text strong style={{ marginTop: '10px', display: 'block' }}>
              Total de Itens: {orderItems.reduce((sum, item) => sum + item.quantity, 0)}
            </Text>
          </Card>
        )}

        <Form.Item>
          <Button type="primary" htmlType="submit" loading={loading} block>
            Registrar Pedido
          </Button>
        </Form.Item>
      </Form>
    </Card>
  );
};

export default OrderForm;
