import React from 'react';

const ErrorTable = ({ jsonError }) => {
    const parsedArray = Object.entries(jsonError).map(([key, message]) => {
        const [rowStr, colStr, colName] = key.split(':');
        return {
            rowNo: Number(rowStr),
            colNo: Number(colStr),
            colName,
            message
        };
    });

    const columnMap = new Map();
    const rowSet = new Set();

    parsedArray.forEach(({ rowNo, colNo, colName }) => {
        rowSet.add(rowNo);
        const colKey = `${colNo}:${colName}`;
        if (!columnMap.has(colKey)) {
            columnMap.set(colKey, { colNo, colName });
        }
    });

    const rowsSorted = Array.from(rowSet).sort((a, b) => a - b);
    const columnsSorted = Array.from(columnMap.values()).sort((a, b) => {
        if (a.colNo !== b.colNo) {
            return a.colNo - b.colNo;
        }
        return a.colName.localeCompare(b.colName);
    });

    const messageLookup = {};
    parsedArray.forEach(({ rowNo, colNo, colName, message }) => {
        const combinedKey = `${rowNo}|${colNo}:${colName}`;
        messageLookup[combinedKey] = message;
    });

    return (
        <div>
            <h5>Error Table</h5>
            <table
                border="1"
                style={{ width: '100%', textAlign: 'left', borderCollapse: 'collapse' }}
            >
                <thead>
                <tr>
                    <th style={{ width: '60px', fontSize: '12px' }}>Row No</th>
                    {columnsSorted.map(({ colNo, colName }) => (
                        <th key={`${colName}`} style={{ fontSize: '12px' }}>
                            {colName}
                        </th>
                    ))}
                </tr>
                </thead>
                <tbody>
                {rowsSorted.map((rowNo) => (
                    <tr key={rowNo}>
                        <td style={{ textAlign: 'center' }}>{rowNo}</td>
                        {columnsSorted.map(({ colNo, colName }) => {
                            const combinedKey = `${rowNo}|${colNo}:${colName}`;
                            return (
                                <td
                                    key={combinedKey}
                                    style={{ fontSize: '12px', color: '#ff2b2b' }}
                                >
                                    {messageLookup[combinedKey] || ''}
                                </td>
                            );
                        })}
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
};

export default ErrorTable;
