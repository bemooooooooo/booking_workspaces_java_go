import './css/Button.css';

const Button = ({ 
  children, 
  variant = 'primary', 
  size = 'medium', 
  disabled = false, 
  type = 'button',
  onClick,
  ...props
}) => {
  return (
    <button
      className={`btn ${variant} ${size}`}
      disabled={disabled}
      type={type}
      onClick={onClick}
      {...props}
    >
      {children}
    </button>
  );
};

export default Button;