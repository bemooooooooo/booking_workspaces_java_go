import Calendar from 'react-calendar';
import { useState} from 'react';
import 'react-calendar/dist/Calendar.css';
import './css/BookingCalendar.css';

const BookingCalendar = ({ onSelect, existingReservations }) => {
  const [date, setDate] = useState(new Date());
  const [timeSlot, setTimeSlot] = useState('');

  const handleDateChange = (newDate) => {
    setDate(newDate);
    setTimeSlot('');
  };

  const handleTimeSelect = (slot) => {
    setTimeSlot(slot);
    const selectedDateTime = new Date(date);
    const [hours, minutes] = slot.split(':');
    selectedDateTime.setHours(parseInt(hours), parseInt(minutes));
    onSelect(selectedDateTime);
  };

  // Проверка, занята ли дата/время
  const isTimeSlotBooked = (slot) => {
    const slotTime = new Date(date);
    const [hours, minutes] = slot.split(':');
    slotTime.setHours(hours, minutes);
    
    return existingReservations.some(reservation => {
      const start = new Date(reservation.startTime);
      const end = new Date(reservation.endTime);
      return slotTime >= start && slotTime <= end;
    });
  };

  const availableTimeSlots = ['09:00', '10:00', '11:00', '12:00', '13:00', '14:00', '15:00', '16:00', '17:00'];

  return (
    <div className="booking-calendar">
      <Calendar 
        onChange={handleDateChange}
        value={date}
        minDate={new Date()}
      />
      
      {date && (
        <div className="time-slots">
          <h3>Доступное время:</h3>
          <div className="slot-grid">
            {availableTimeSlots.map(slot => (
              <button
                key={slot}
                className={`slot-btn ${timeSlot === slot ? 'selected' : ''} ${isTimeSlotBooked(slot) ? 'booked' : ''}`}
                onClick={() => !isTimeSlotBooked(slot) && handleTimeSelect(slot)}
                disabled={isTimeSlotBooked(slot)}
              >
                {slot}
                {isTimeSlotBooked(slot) && <span className="booked-label">Занято</span>}
              </button>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

export default BookingCalendar;