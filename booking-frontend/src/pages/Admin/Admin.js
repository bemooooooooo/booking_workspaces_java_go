import { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import { workspaceService } from '../../services/Workspace';
import WorkspaceForm from '../../components/Admin/WorkspaceForm';
import WorkspaceList from '../../components/Admin/WorkspaceList';
import Button from '../../components/UI/Button';
import Modal from '../../components/UI/Modal';
import './Admin.css';

const Admin = () => {
  const { user } = useAuth();
  const [workspaces, setWorkspaces] = useState([]);
  const [editingWorkspace, setEditingWorkspace] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  // Загрузка рабочих мест
  useEffect(() => {
    const loadWorkspaces = async () => {
      try {
        const data = await workspaceService.getAllWorkspaces();
        setWorkspaces(data);
      } catch (err) {
        setError('Не удалось загрузить рабочие места');
        console.error('Ошибка загрузки:', err);
      } finally {
        setLoading(false);
      }
    };

    if (user?.role === 'admin') {
      loadWorkspaces();
    }
  }, [user]);

  // Создание/обновление рабочего места
  const handleSaveWorkspace = async (workspaceData) => {
    try {
      setLoading(true);
      setError('');

      let updatedWorkspace;
      if (editingWorkspace?.id) {
        updatedWorkspace = await workspaceService.updateWorkspace(editingWorkspace.id, workspaceData);
        setWorkspaces(workspaces.map(ws => 
          ws.id === updatedWorkspace.id ? updatedWorkspace : ws
        ));
      } else {
        updatedWorkspace = await workspaceService.createWorkspace(workspaceData);
        setWorkspaces([...workspaces, updatedWorkspace]);
      }

      setIsModalOpen(false);
      setEditingWorkspace(null);
    } catch (err) {
      setError(err.message || 'Ошибка сохранения рабочего места');
    } finally {
      setLoading(false);
    }
  };

  // Деактивация рабочего места
  const handleDeactivate = async (id) => {
    try {
      setLoading(true);
      await workspaceService.deactivateWorkspace(id);
      setWorkspaces(workspaces.filter(ws => ws.id !== id));
    } catch (err) {
      setError('Не удалось деактивировать рабочее место');
    } finally {
      setLoading(false);
    }
  };

  if (user?.role !== 'admin') {
    return (
      <div className="admin-container">
        <h2>Доступ запрещен</h2>
        <p>Требуются права администратора</p>
      </div>
    );
  }

  if (loading && workspaces.length === 0) {
    return <div className="loading-spinner">Загрузка...</div>;
  }
  console.log(user)
  return (
    <div className="admin-container">
      <h1>Панель администратора</h1>
      <h2>Управление рабочими местами</h2>

      {error && <div className="error-message">{error}</div>}

      <div className="admin-actions">
        <Button 
          onClick={() => {
            setEditingWorkspace({});
            setIsModalOpen(true);
          }}
        >
          Добавить рабочее место
        </Button>
      </div>

      <WorkspaceList
        workspaces={workspaces}
        onEdit={(workspace) => {
          setEditingWorkspace(workspace);
          setIsModalOpen(true);
        }}
        onDeactivate={handleDeactivate}
      />

      <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)}>
        <WorkspaceForm
          workspace={editingWorkspace}
          onSave={handleSaveWorkspace}
          onCancel={() => {
            setIsModalOpen(false);
            setEditingWorkspace(null);
          }}
          loading={loading}
        />
      </Modal>
    </div>
  );
};

export default Admin;