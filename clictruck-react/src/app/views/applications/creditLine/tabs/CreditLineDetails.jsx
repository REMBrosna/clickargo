import { Grid, InputAdornment } from "@material-ui/core";
import {
  Description,
  LocalAtmOutlined,
  MenuOutlined,
} from "@material-ui/icons";
import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import C1DataTable from "app/c1component/C1DataTable";
import C1InputField from "app/c1component/C1InputField";
import C1TabContainer from "app/c1component/C1TabContainer";
import moment from "moment/moment";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import NumFormat from "app/clictruckcomponent/NumFormat";
import { customFilterDateDisplay, formatCurrency } from "app/c1utils/utility";
import { CreditTransactionTypes } from "app/c1utils/const";

const CreditLineDetails = (props) => {
  const { data } = props;
  console.log("CreditLineDetails", data);
  const [creditData, setCreditData] = useState({});
  const [isOpm, setIsOpm] = useState(false);
  const [orderBy, setOrderBy] = useState();

  const { t } = useTranslation([
    "buttons",
    "listing",
    "administration",
    "common",
  ]);

  const toTitleCase = (str) => {
    const titleCase = str
      .toLowerCase()
      .split(" ")
      .map((word) => {
        return word.charAt(0).toUpperCase() + word.slice(1);
      })
      .join(" ");

    return titleCase;
  };

  const columns = [
    {
      name: isOpm ? "opmjTxnRef" : "cjnTxnRef",
      label: "Txn Ref",
      options: {
        filter: false,
        sort: false,
      },
    },
    {
      name: "tckMstJournalTxnType.jttId",
      label: t("listing:creditLines.txtType"),
      options: {
        filter: true,
        filterType: "dropdown",
        filterOptions: {
          names: Object.keys(CreditTransactionTypes),
          renderValue: (v) => CreditTransactionTypes[v].desc,
        },
        customFilterListOptions: {
          render: (v) => {
            return CreditTransactionTypes[v].desc;
          },
        },
        customBodyRender: (value, tableMeta, updateValue) => {
          if (value) return CreditTransactionTypes[value].desc;
          else return value;
        },
      },
    },
    {
      name: "tckMstJournalTxnType.jttName",
      label: t("listing:creditLines.desc"),
      options: {
        filter: true,
        customBodyRender: (value, tableMeta, updateValue) => {
          if (value) return CreditTransactionTypes[value].desc;
          else return value;
        },
      },
    },
    {
      name: isOpm ? "opmjReserve" : "cjnReserve",
      label: t("listing:creditLines.onHold"),
      options: {
        filter: true,
        customBodyRender: (value, tableMeta, updateValue) => {
          if (!value) return 0;
          return formatCurrency(value, "IDR");
        },
      },
    },
    {
      name: isOpm ? "opmjUtilized" : "cjnUtilized",
      label: t("listing:creditLines.utilized"),
      options: {
        filter: true,
        customBodyRender: (value, tableMeta, updateValue) => {
          if (!value) return 0;
          return formatCurrency(value, "IDR");
        },
      },
    },

    {
      name: isOpm ? "opmjDtCreate" : "cjnDtCreate",
      label: t("listing:creditLines.txnDate"),
      options: {
        filter: true,
        display: true,
        customBodyRender: (value, tableMeta, updateValue) => {
          return moment(value).format("DD/MM/YYYY HH:mm:ss");
        },
        filterType: "custom",
        filterOptions: {
          display: customFilterDateDisplay,
        },
      },
    },
  ];

  useEffect(() => {
    setCreditData(data);
    console.log("data.isopm", data?.isOpm);
    setIsOpm(data?.isOpm ?? false);
    setOrderBy(data?.isOpm ? "opmjId" : "cjnId");
  }, [data]);

  return (
    <>
      <Grid item xs={12}>
        <C1TabContainer>
          <Grid item md={6} xs={12}>
            <C1CategoryBlock
              icon={<Description />}
              title={t("listing:creditLines.generalDetails")}
            >
              {/* <C1InputField
                label={`Id`}
                value={creditData?.crId}
                name="id"
                disabled
                onChange={() => console.log()}
              /> */}
              <C1InputField
                label={t("listing:creditLines.creditAmount")}
                value={isOpm ? creditData?.opmAmt : creditData?.crAmt}
                name="crsAmt"
                disabled
                onChange={() => console.log()}
                inputProps={{ style: { textAlign: "right" } }}
                InputProps={{
                  inputComponent: NumFormat,
                  startAdornment: (
                    <InputAdornment
                      position="start"
                      style={{ paddingRight: "8px" }}
                    >
                      Rp
                    </InputAdornment>
                  ),
                }}
              />
              <C1InputField
                label={t("listing:creditLines.startDate")}
                value={
                  creditData?.crDtStart
                    ? moment(
                        isOpm ? creditData?.opmDtStart : creditData?.crDtStart
                      ).format("DD/MM/YYYY")
                    : ""
                }
                name="crDtStart"
                disabled
                onChange={() => console.log()}
              />
              <C1InputField
                label={t("listing:creditLines.approvedBy")}
                value={
                  isOpm
                    ? creditData?.opmUsrApprove
                    : creditData?.tcoreUsrApprove?.usrName
                }
                name="crUsrApprove"
                disabled
                onChange={() => console.log()}
              />
            </C1CategoryBlock>
          </Grid>

          <Grid item md={6} xs={12}>
            <C1CategoryBlock
              icon={<LocalAtmOutlined />}
              title={t("listing:creditLines.creditBalance")}
            >
              <C1InputField
                label={t("listing:creditLines.utilized")}
                value={
                  isOpm
                    ? creditData?.opmSummary?.opmsUtilized
                    : creditData?.tckCreditSummary?.crsUtilized
                }
                name="crsUtilized"
                disabled
                onChange={() => console.log()}
                inputProps={{ style: { textAlign: "right" } }}
                InputProps={{
                  inputComponent: NumFormat,
                  startAdornment: (
                    <InputAdornment
                      position="start"
                      style={{ paddingRight: "8px" }}
                    >
                      Rp
                    </InputAdornment>
                  ),
                }}
              />
              <C1InputField
                label={t("listing:creditLines.reserved")}
                value={
                  isOpm
                    ? creditData?.opmSummary?.opmsReserve
                    : creditData?.tckCreditSummary?.crsReserve
                }
                name="crsReserve"
                disabled
                onChange={() => console.log()}
                inputProps={{ style: { textAlign: "right" } }}
                InputProps={{
                  inputComponent: NumFormat,
                  startAdornment: (
                    <InputAdornment
                      position="start"
                      style={{ paddingRight: "8px" }}
                    >
                      Rp
                    </InputAdornment>
                  ),
                }}
              />
              <C1InputField
                label={t("listing:creditLines.balance")}
                value={
                  isOpm
                    ? creditData?.opmSummary?.opmsBalance
                    : creditData?.tckCreditSummary?.crsBalance
                }
                name="crsBalance"
                disabled
                onChange={() => console.log()}
                inputProps={{ style: { textAlign: "right" } }}
                InputProps={{
                  inputComponent: NumFormat,
                  startAdornment: (
                    <InputAdornment
                      position="start"
                      style={{ paddingRight: "8px" }}
                    >
                      Rp
                    </InputAdornment>
                  ),
                }}
              />
            </C1CategoryBlock>
          </Grid>

          <Grid item xs={12}>
            <C1CategoryBlock
              icon={<MenuOutlined />}
              title={t("listing:creditLines.creditLineDeb")}
            >
              {/* /api/v1/clickargo/credit/${data?.product}/${data?.company}/${data?.currency} */}
              <C1DataTable
                url={
                  isOpm
                    ? `/api/v1/clickargo/opm/credit/journal`
                    : `/api/v1/clickargo/journal`
                }
                isServer={true}
                columns={columns}
                defaultOrder={orderBy}
                defaultOrderDirection="desc"
                isShowFilterChip
                isShowToolbar={false}
                isShowDownload={false}
                isShowPrint={false}
                isRowSelectable={false}
                guideId="clicdo.doi.co.jobs.list.table"
                filterBy={[
                  {
                    attribute: "TCkMstServiceType.svctId",
                    value: data?.product,
                  },
                  { attribute: "TMstCurrency.ccyCode", value: data?.currency },
                  {
                    attribute: "TCoreAccn.accnId",
                    value: creditData?.company,
                  },
                ]}
                customRowsPerPage={[80, 100]}
                customPerPage={80}
              />
            </C1CategoryBlock>
          </Grid>
        </C1TabContainer>
      </Grid>
    </>
  );
};

export default CreditLineDetails;
