import './css/Spinner.css';

const Spinner = ({ size = 'medium' }) => {
  return (
    <div className={`spiner ${size}`}>
      <div className="spiner-inner"></div>
    </div>
  );
};

export default Spinner;