import useHttp from "app/c1hooks/http";
import CreditDashboard from "app/clictruckcomponent/Credit/CreditDashboard";
import OPMCreditDashboard from "app/clictruckcomponent/Credit/OPMCreditDashboard";
import useAuth from "app/hooks/useAuth";
import React, { useEffect, useState } from "react";

const DashboardCredit = (props) => {
  const { opm } = props;
  const { isLoading, res, error, urlId, sendRequest } = useHttp();
  const [loading, setLoading] = useState(false);
  const [creditData, setCreditData] = useState({});
  const { user } = useAuth();
  const [checked, setChecked] = useState(false);

  const handleChangeCap = (event) => {
    setChecked(event?.target?.checked);
  };

  const getCreditBalance = () => {
    setLoading(true);
    if (opm) {
      const payload = {
        tckMstServiceType: {
          svctId: "CLICTRUCK",
        },
        tcoreAccn: {
          accnId: user?.coreAccn?.accnId,
        },
      };
      sendRequest(
        `/api/v1/clickargo/opm/credit/find`,
        "getOpmCreditData",
        "POST",
        payload
      );
    } else {
      const payload = {
        tckMstServiceType: {
          svctId: "CLICTRUCK",
        },
        tcoreAccn: {
          accnId: user?.coreAccn?.accnId,
        },
        tmstCurrency: {
          ccyCode: "IDR",
        },
      };
      sendRequest(
        `/api/v1/clickargo/credit/find`,
        "getCreditData",
        "POST",
        payload
      );
    }
  };

  const handleRefresh = () => {
    getCreditBalance();
  };

  useEffect(() => {
    getCreditBalance();
    // eslint-disable-next-line
  }, []);

  useEffect(() => {
    if (!isLoading && !error && res) {
      switch (urlId) {
        case "getCreditData":
          const data = {
            ...res?.data,
            ...{
              product: "CLICTRUCK",
              company: user?.coreAccn?.accnId,
              currency: "IDR",
            },
          };
          setCreditData(data);
          setLoading(false);
          break;
        case "getOpmCreditData": {
          const data = {
            ...res?.data,
            ...{
              product: "CLICTRUCK",
              company: user?.coreAccn?.accnId,
              currency: "IDR",
            },
          };
          setCreditData(data);
          setLoading(false);
        }

        default:
          break;
      }
    }

    if (error) {
      setLoading(false);
    }

    // eslint-disable-next-line
  }, [isLoading, error, res, urlId]);

  return (
    <>
      {opm ? (
        <OPMCreditDashboard
          data={creditData}
          historyRoute="/applications/creditline"
          handleRefresh={handleRefresh}
          isRefresh={loading}
          handleChangeCap={handleChangeCap}
        />
      ) : (
        <CreditDashboard
          data={creditData}
          historyRoute="/applications/creditline"
          handleRefresh={handleRefresh}
          isRefresh={loading}
          showCap={creditData?.crTxnCap !== null}
          isCap={checked}
          handleChangeCap={handleChangeCap}
        />
      )}
    </>
  );
};

export default DashboardCredit;
