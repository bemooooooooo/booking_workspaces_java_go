import { Routes, Route } from 'react-router-dom';
import Home from './pages/Home';
import Booking from './pages/Bookings';
import Login from './pages/Login';
import Profile from './pages/Profile';
import PrivateRoute from './PrivateRoute';
import Register from './pages/Register';
import Admin from './pages/Admin/Admin';

const AppRoutes = () => {
  return (
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route path="/bookings" element={
        <PrivateRoute>
          <Booking />
        </PrivateRoute>
      } />
      <Route path="/profile" element={
        <PrivateRoute>
          <Profile />
        </PrivateRoute>
      } />
      <Route path="/admin" element={
        <PrivateRoute>
          <Admin />
        </PrivateRoute>
      } />
    </Routes>
  );
};

export default AppRoutes;