import './css/BookingConfirmation.css';

const BookingConfirmation = ({ dateTime, workspace, onConfirm }) => {
  return (
    <div className="booking-confirmation">
      <h2>Подтверждение бронирования</h2>
      
      <div className="booking-details">
        <h3>Детали брони:</h3>
        <p><strong>Дата и время:</strong> {dateTime.toLocaleString()}</p>
        <p><strong>Рабочее место:</strong> {workspace.name}</p>
      </div>

      <button 
        className="confirm-button"
        onClick={onConfirm}
      >
        Подтвердить бронирование
      </button>
    </div>
  );
};

export default BookingConfirmation;