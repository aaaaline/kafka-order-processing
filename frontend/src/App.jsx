import React from 'react';
import { Layout, Typography, Space } from 'antd';
import OrderForm from './components/OrderForm';
import NotificationDisplay from './components/NotificationDisplay';
import './App.css';

const { Header, Content, Footer } = Layout;
const { Title } = Typography;

function App() {
  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Header style={{ background: '#001529', padding: '0 20px', display: 'flex', alignItems: 'center' }}>
        <Title level={3} style={{ color: '#fff', margin: 0 }}>
          Pedidos Kafka
        </Title>
      </Header>
      <Content style={{ padding: '50px', display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
        <Space direction="vertical" size={30} style={{ width: '100%', maxWidth: '600px' }}>
          <OrderForm />
          <NotificationDisplay />
        </Space>
      </Content>
      <Footer style={{ textAlign: 'center' }}>
        Sistema de Pedidos Kafka ©{new Date().getFullYear()}
      </Footer>
    </Layout>
  );
}

export default App;
