import React from 'react';
import { Layout, Typography } from 'antd';
import OrderForm from './components/OrderForm';
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
      <Content style={{ padding: '50px', display: 'flex', justifyContent: 'center', alignItems: 'flex-start' }}>
        <OrderForm />
      </Content>
      <Footer style={{ textAlign: 'center' }}>
        Sistema de Pedidos Kafka ©{new Date().getFullYear()}
      </Footer>
    </Layout>
  );
}

export default App;
