import { useContext } from 'react';
import StatsContext from '../context/StatsContext';

const useStats = () => useContext(StatsContext);

export default useStats;
