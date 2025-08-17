import Button from '../UI/Button';
import './css/WorkspaceList.css';

const WorkspaceList = ({ workspaces, onEdit, onDeactivate }) => {
  if (workspaces.length === 0) {
    return <p>Нет рабочих мест для отображения</p>;
  }

  return (
    <div className="workspace-list">
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Название</th>
            <th>Описание</th>
            <th>Вместимость</th>
            <th>Активно</th>
          </tr>
        </thead>
        <tbody>
          {workspaces.map(workspace => (
            <tr key={workspace.id}>
              <td>{workspace.id}</td>
              <td>{workspace.name}</td>
              <td>{workspace.description}</td>
              <td>{workspace.capacity}</td>
              <td>
                {workspace.isActive === 'true' && 'Активно'}
                {workspace.isActive === 'false' && 'Неактивно'}
              </td>
              <td className="actions">
                <Button
                  variant="secondary"
                  size="small"
                  onClick={() => onEdit(workspace)}
                >
                  Редактировать
                </Button>
                <Button
                  variant="danger"
                  size="small"
                  onClick={() => onDeactivate(workspace.id)}
                >
                  Деактивировать
                </Button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default WorkspaceList;