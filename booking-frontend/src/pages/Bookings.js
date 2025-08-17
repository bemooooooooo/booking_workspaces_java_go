import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { bookingService } from '../services/Booking';
import { workspaceService }from '../services/Workspace';
import { formatDateTime } from '../utils/dateUtils.js';
import BookingCalendar from '../components/Booking/BookingCalendar';
import WorkspaceSelector from '../components/Booking/WorkspaceSelector';
import BookingConfirmation from '../components/Booking/BookingConfirmation';
import Button from '../components/UI/Button';
import Spinner from '../components/UI/Spinner';
import './css/Bookings.css';

const Bookings = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [step, setStep] = useState(1); // 1 - время, 2 - место, 3 - подтверждение
  const [selectedDateTime, setSelectedDateTime] = useState(null);
  const [selectedWorkspace, setSelectedWorkspace] = useState(null);
  const [availableWorkspaces, setAvailableWorkspaces] = useState([]);
  const [userReservations, setUserReservations] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    // Загрузка текущих бронирований пользователя
    const loadUserReservations = async () => {
      try {
        const reservations = await bookingService.getUserActiveReservations();
        setUserReservations(reservations);
      } catch (err) {
        setError('Не удалось загрузить ваши бронирования');
      }
    };
    loadUserReservations();
  }, []);

  // Загрузка доступных мест при выборе времени
  const handleTimeSelect = async (dateTime) => {
    setLoading(true);
    setError('');
    try {
      setSelectedDateTime(dateTime);

      const endTime = new Date(dateTime);
      endTime.setHours(endTime.getHours() + 1); // По умолчанию +1 час

      const available = await workspaceService.getAvailableWorkspaces(dateTime, endTime);

      setAvailableWorkspaces(available);
      setStep(2);
    } catch (err) {
      setError('Не удалось проверить доступность мест');
    } finally {
      setLoading(false);
    }
  };

  const handleWorkspaceSelect = (workspace) => {
    setSelectedWorkspace(workspace);
    setStep(3);
  };

  const handleConfirmBooking = async () => {
    if (!user || !selectedDateTime || !selectedWorkspace) return;
    
    setLoading(true);
    setError('');
    
    try {
      const endTime = new Date(selectedDateTime);
      endTime.setHours(endTime.getHours() + 1);
      
      await bookingService.createReservation({
        workspaceId: selectedWorkspace.id,
        startTime: formatDateTime(selectedDateTime),
        endTime: formatDateTime(endTime)
      });
      
      navigate('/profile', { 
        state: { 
          bookingSuccess: true,
          message: `Бронирование места ${selectedWorkspace.name} подтверждено` 
        } 
      });
    } catch (err) {
      setError('Не удалось завершить бронирование. Попробуйте позже.');
    } finally {
      setLoading(false);
    }
  };

  const handleBack = () => {
    if (step > 1) {
      setStep(step - 1);
    } else {
      navigate('/');
    }
  };

  return (
    <div className="bookings-page">
      <h1>Бронирование рабочего места</h1>
      
      <div className="booking-steps">
        <div className={`step ${step >= 1 ? 'active' : ''}`}>
          1. Выбор времени
        </div>
        <div className={`step ${step >= 2 ? 'active' : ''}`}>
          2. Выбор места
        </div>
        <div className={`step ${step >= 3 ? 'active' : ''}`}>
          3. Подтверждение
        </div>
      </div>

      {error && <div className="error-message">{error}</div>}

      {loading ? (
        <div className="loading-overlay">
          <Spinner size="large" />
        </div>
      ) : (
        <>
          {step === 1 && (
            <div className="step-content">
              <h2>Выберите дату и время</h2>
              <BookingCalendar 
                onSelect={handleTimeSelect}
                existingReservations={userReservations}
              />
            </div>
          )}

          {step === 2 && (
            <div className="step-content">
              <h2>Выберите рабочее место</h2>
              <p>Доступные места на {selectedDateTime.toLocaleString()}</p>
              <WorkspaceSelector
                workspaces={availableWorkspaces}
                onSelect={handleWorkspaceSelect}
              />
            </div>
          )}

          {step === 3 && (
            <div className="step-content">
              <BookingConfirmation
                dateTime={selectedDateTime}
                workspace={selectedWorkspace}
                onConfirm={handleConfirmBooking}
              />
            </div>
          )}
        </>
      )}

      <div className="booking-actions">
        <Button variant="secondary" onClick={handleBack}>
          Назад
        </Button>
      </div>
    </div>
  );
};

export default Bookings;