import { useState, useEffect } from 'react';
import Button from '../UI/Button';
import './css/WorkspaceForm.css';

const WorkspaceForm = ({ workspace, onSave, onCancel, loading }) => {
  const [formData, setFormData] = useState({
    name: '',
    capacity: 1,
    isActive: false,
    createdAt: Date.now(),
    description: ''
  });

  useEffect(() => {
    if (workspace) {
      setFormData({
        name: workspace.name || '',
        capacity: workspace.capacity || 1,
        isActive: workspace.isActive || false,
        createdAt: workspace.createdAt || Date.now(),
        description: workspace.description || ''
      });
    }
  }, [workspace]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    onSave(formData);
  };

  return (
    <form className="workspace-form" onSubmit={handleSubmit}>
      <h2>{workspace?.id ? 'Редактирование' : 'Новое рабочее место'}</h2>

      <div className="form-group">
        <label>Название:
          <input
            type="text"
            name="name"
            value={formData.name}
            onChange={handleChange}
            required
          />
        </label>
      </div>

      <div className="form-group">
        <label>Активно:
          <select
            name="isActive"
            value={formData.isActive}
            onChange={handleChange}
            required
          >
            <option value="true">Активно</option>
            <option value="false">Неактивно</option>
          </select>
        </label>
      </div>

      <div className="form-group">
        <label>Вместимость:
          <input
            type="number"
            name="capacity"
            min="1"
            max="20"
            value={formData.capacity}
            onChange={handleChange}
            required
          />
        </label>
      </div>

      <div className="form-group">
        <label>Описание:
          <textarea
            name="description"
            value={formData.description}
            onChange={handleChange}
            rows="3"
          />
        </label>
      </div>

      <div className="form-actions">
        <Button type="button" variant="secondary" onClick={onCancel}>
          Отмена
        </Button>
        <Button type="submit" disabled={loading}>
          {loading ? 'Сохранение...' : 'Сохранить'}
        </Button>
      </div>
    </form>
  );
};

export default WorkspaceForm;