import React, { useState, useEffect } from 'react';
import { Button, Card, notification, Spin, Typography } from 'antd';
import { BellOutlined } from '@ant-design/icons';

const { Text } = Typography;

const NotificationDisplay = () => {
  const [latestNotification, setLatestNotification] = useState(null);
  const [fetchingNotification, setFetchingNotification] = useState(false);
  const NOTIFICATION_BACKEND_URL = 'http://localhost:8083';

  const fetchLatestNotification = async () => {
    setFetchingNotification(true);
    try {
      const response = await fetch(`${NOTIFICATION_BACKEND_URL}/notifications/latest`);
      if (response.ok) {
        const data = await response.json();
        if (data && (JSON.stringify(data) !== JSON.stringify(latestNotification))) {
          setLatestNotification(data);
          notification.info({
            message: `Notificação de Pedido: ${data.orderId.substring(0, 8)}...`,
            description: `Status: ${data.status === 'SUCCESS' ? 'Confirmado' : 'Falha'}. Cliente: ${data.customerName}. ${data.message ? `Motivo: ${data.message}` : ''}`,
            placement: 'bottomRight',
            duration: 0
          });
        }
      } else if (response.status === 204) {
        setLatestNotification(null);
      } else {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
    } catch (error) {
      console.error("Erro ao buscar notificação:", error);
      notification.error({
        message: "Erro na Notificação",
        description: "Não foi possível buscar as últimas notificações. Verifique se o Notification-Service está online na porta 8083.",
        placement: 'bottomRight'
      });
    } finally {
      setFetchingNotification(false);
    }
  };

  useEffect(() => {
    const interval = setInterval(fetchLatestNotification, 5000);
    return () => clearInterval(interval);
  }, [latestNotification]);

  return (
    <Card title="Status das Notificações" style={{ maxWidth: '600px', margin: '20px auto' }}>
      <Button
        type="primary"
        icon={<BellOutlined />}
        onClick={fetchLatestNotification}
        loading={fetchingNotification}
      >
        Verificar Notificações Agora
      </Button>
      {latestNotification && (
        <div style={{ marginTop: '15px', padding: '10px', border: '1px solid #e8e8e8', borderRadius: '4px' }}>
          <Text strong>Última Notificação Recebida:</Text><br />
          <Text strong>Pedido ID: </Text><Text>{latestNotification.orderId.substring(0, 8)}...</Text><br />
          <Text strong>Cliente: </Text><Text>{latestNotification.customerName}</Text><br />
          <Text strong>Status: </Text>
          <Text style={{ color: latestNotification.status === 'SUCCESS' ? 'green' : 'red' }}>
            {latestNotification.status === 'SUCCESS' ? 'SUCESSO' : 'FALHA'}
          </Text><br />
          {latestNotification.message && (
            <>
              <Text strong>Mensagem: </Text><Text>{latestNotification.message}</Text><br />
            </>
          )}
        </div>
      )}
      {!latestNotification && !fetchingNotification && <Text style={{ marginTop: '15px', display: 'block' }}>Nenhuma notificação recente para exibir.</Text>}
    </Card>
  );
};

export default NotificationDisplay;
