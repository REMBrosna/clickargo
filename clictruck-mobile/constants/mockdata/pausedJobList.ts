interface PausedListData {
    jobId: string;
    jobType: string;
    locFrom: string;
    locTo: string;
    timePickUp:number;
    timeDropOff:number;
    time1?:  null | number;
    time2?:  null | number;
    time3?:  null | number;
    time4?:  null | number;
    dataCargo?: object
    dataRemarks?: object
}

const PAUSED_DATA: PausedListData[]= [
    {
        jobId: "CKCTJ2023101900001",
        jobType: "DOMESTIC",
        locFrom: "The Prime Office Suite Yos Sudarso Kav. 30 Street, 7th Floor, RT.10/RW.11, Sunter Jaya, Kec. Tj. Priok, Jkt Utara, Daerah Khusus Ibukota Jakarta 14330",
        locTo: "Jl. Raya Daan Mogot No.2, RT.2/RW.4, Kalideres, Kec. Kalideres, Kota Jakarta Barat",
        timePickUp: 1664557200000,
        timeDropOff: 1674458200000,
        time1: 1664557200000,
        time2: 1664557200000,
        time3: 1674358200000,
        time4: null,
    },
    {
        jobId: "CKCTJ2023101900002",
        jobType: "DOMESTIC",
        locFrom: "The Prime Office Suite Yos Sudarso Kav. 30 Street, 7th Floor, RT.10/RW.11, Sunter Jaya, Kec. Tj. Priok, Jkt Utara, Daerah Khusus Ibukota Jakarta 14330",
        locTo: "Jl. Raya Daan Mogot No.2, RT.2/RW.4, Kalideres, Kec. Kalideres, Kota Jakarta Barat",
        timePickUp: 1664557200000,
        timeDropOff: 1674458200000,
        time1: 1664557200000,
        time2: 1664557200000,
        time3: 1674358200000,
        time4: null,
    }
]

export {PausedListData, PAUSED_DATA}