import { useState} from 'react';
import './css/WorkspaceSelector.css';

const WorkspaceSelector = ({ workspaces, onSelect }) => {
  const [selectedId, setSelectedId] = useState(null);

  const handleSelect = (workspace) => {
    setSelectedId(workspace.id);
    onSelect(workspace);
  };

  return (
    <div className="workspace-selector">
      {workspaces.length === 0 ? (
        <p>Нет доступных рабочих мест на выбранное время</p>
      ) : (
        <div className="workspace-grid">
          {workspaces.map(workspace => (
            <button
              type="button"
              key={workspace.id}
              className={`workspace-card ${selectedId === workspace.id ? 'selected' : ''}`}
              onClick={() => handleSelect(workspace)}
            >
              <h3>{workspace.name}</h3>
              <p>Тип: {workspace.type}</p>
            </button>
          ))}
        </div>
      )}
    </div>
  );
};

export default WorkspaceSelector;