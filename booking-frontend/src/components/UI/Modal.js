import { useEffect } from 'react';
import './css/Modal.css';
import Button from './Button';

const Modal = ({
  isOpen,
  onClose,
  title,
  children,
  closeOnBackdropClick = true,
  showCloseButton = true,
  width = '600px',
  maxHeight = '80vh'
}) => {
  useEffect(() => {
    if (isOpen) {
      document.body.style.overflow = 'hidden';
    } else {
      document.body.style.overflow = 'auto';
    }

    return () => {
      document.body.style.overflow = 'auto';
    };
  }, [isOpen]);

  if (!isOpen) return null;

  const handleBackdropClick = (e) => {
    if (e.target === e.currentTarget && closeOnBackdropClick) {
      onClose();
    }
  };

  return (
    <div className="modal-backdrop" 
         onClick={handleBackdropClick} onKeyDown={(e) => e.key === 'Enter' && handleBackdropClick()}
         role="button"
         tabIndex={0}
         >
      <div
        className="modal-content"
        style={{
          width: width,
          maxHeight: maxHeight
        }}
      >
        {(title || showCloseButton) && (
          <div className="modal-header">
            {title && <h2 className="modal-title">{title}</h2>}
            {showCloseButton && (
              <Button
                variant="text"
                onClick={onClose}
                className="modal-close-button"
              >
                &times;
              </Button>
            )}
          </div>
        )}

        <div className="modal-body">
          {children}
        </div>
      </div>
    </div>
  );
};

export default Modal;