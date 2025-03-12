import React from "react";
import GridActionButton from "./GridActionButton";
import C1DataTable from "app/c1component/C1DataTable";
import PropTypes from "prop-types";

const DataTable = (props) => {
  const {
    url,
    columns,
    title,
    defaultOrder,
    defaultOrderDirection,
    isServer,
    isShowViewColumns,
    isShowDownload,
    isShowPrint,
    isShowFilter,
    isRefresh,
    isShowFilterChip,
    filterBy,
    data,
    showAddButton,
    showActiveHistoryButton,
    viewTextFilter,
    customRowsPerPage,
    showMultiSelectActionButton,
    showTemplate,
    showSubTile = false,
    subTile = "",
  } = props;

  return (
    <>
      {(showAddButton ||
        showActiveHistoryButton ||
        showMultiSelectActionButton) && (
        <GridActionButton
          showAddButton={showAddButton}
          showActiveHistoryButton={showActiveHistoryButton}
          showMultiSelectActionButton={showMultiSelectActionButton}
        />
      )}
      {showSubTile ? subTile : ""}
      <C1DataTable
        dbName={data}
        url={url}
        columns={columns}
        title={title}
        defaultOrder={defaultOrder}
        defaultOrderDirection={defaultOrderDirection}
        isServer={isServer}
        isShowViewColumns={isShowViewColumns}
        isShowDownload={isShowDownload}
        isShowPrint={isShowPrint}
        isShowFilter={isShowFilter}
        isRefresh={isRefresh}
        isShowFilterChip={isShowFilterChip}
        filterBy={filterBy}
        guideId={""}
        viewTextFilter={viewTextFilter}
        customRowsPerPage={customRowsPerPage}
        showTemplate={showTemplate}
      />
    </>
  );
};

DataTable.propTypes = {
  isServer: PropTypes.bool,
  dbName: PropTypes.exact({
    list: PropTypes.array,
  }),
  url: PropTypes.string,
  title: PropTypes.any,
  columns: PropTypes.array.isRequired,
  defaultOrder: PropTypes.string,
  defaultOrderDirection: PropTypes.oneOf(["asc", "desc"]),
  showTemplate: PropTypes.exact({
    downloadDataHandler: PropTypes.object,
    downloadHandler: PropTypes.func || PropTypes.object,
    uploadHandler: PropTypes.func || PropTypes.object,
  }),
  showCustomDownload: PropTypes.exact({
    title: PropTypes.string,
    handler: PropTypes.func,
  }),
  showManualAdd: PropTypes.shape({
    path: PropTypes.string,
    popUpHandler: PropTypes.func,
  }),
  showBlClaim: PropTypes.shape({
    type: PropTypes.oneOf(["popUp", "redirect"]),
    path: PropTypes.string,
    popUpHandler: PropTypes.func,
  }),
  showHistory: PropTypes.shape({
    type: PropTypes.oneOf(["redirect"]),
    path: PropTypes.string,
    title: PropTypes.string,
  }),
  viewHistory: PropTypes.exact({
    title: PropTypes.string,
    handler: PropTypes.func,
    icon: PropTypes.any,
  }),
  viewTextFilter: PropTypes.element,
  showAdd: PropTypes.shape({
    type: PropTypes.oneOf(["popUp", "redirect"]),
    path: PropTypes.string,
    popUpHandler: PropTypes.func,
  }),
  showClaim: PropTypes.shape({
    type: PropTypes.oneOf(["popUp", "redirect"]),
    path: PropTypes.string,
    popUpHandler: PropTypes.func,
  }),
  showFfSubmit: PropTypes.shape({
    type: PropTypes.oneOf(["popUp", "redirect"]),
    path: PropTypes.string,
    popUpHandler: PropTypes.func,
  }),
  showVerify: PropTypes.shape({
    type: PropTypes.oneOf(["popUp", "redirect"]),
    path: PropTypes.string,
    popUpHandler: PropTypes.func,
  }),
  showAction: PropTypes.shape({
    type: PropTypes.oneOf(["popUp", "redirect"]),
    path: PropTypes.string,
    popUpHandler: PropTypes.func,
  }),
  isShowToolbar: PropTypes.bool,
  isShowDownloadData: PropTypes.bool,
  isShowFilter: PropTypes.bool,
  isShowFilterChip: PropTypes.bool,
  isShowViewColumns: PropTypes.bool,
  isShowDownload: PropTypes.bool,
  isShowPrint: PropTypes.bool,
  isRowSelectable: PropTypes.bool,
  isShowPagination: PropTypes.bool,
  filterBy: PropTypes.array,
  onFilterChange: PropTypes.func,
  onFilterChipClose: PropTypes.func,
  guideId: PropTypes.string,
  showPay: PropTypes.exact({
    title: PropTypes.string,
    handler: PropTypes.func,
    icon: PropTypes.any,
  }),
  // showAddButton: PropTypes.shape({
  //     show: PropTypes.bool,
  //     label: PropTypes.string,
  //     icon: PropTypes.element,
  //     action: PropTypes.func
  // }),
  showAddButton: PropTypes.array,
  showActiveHistoryButton: PropTypes.func,
  customRowsPerPage: PropTypes.array,
};

export default DataTable;
