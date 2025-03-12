interface HistoryJobProps {
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

export const HISTORY_DATA: HistoryJobProps[] = [
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
        time4: 1674558200000,
        dataCargo:{
            type: "A",
            weight: "1000",
            volumetric: "1000",
            length: "2000",
            width: "400",
            height: "150",
            desc: "test test test",
            remarks: "test test test"
        },
        dataRemarks:{
            remarks: "test test test",
            special: "test test test"
        }
    },
    {
        jobId: "CKCTJ2023101900002",
        jobType: "EXPORT",
        locFrom: "The Prime Office Suite Yos Sudarso Kav. 30 Street, 7th Floor, RT.10/RW.11, Sunter Jaya, Kec. Tj. Priok, Jkt Utara, Daerah Khusus Ibukota Jakarta 14330",
        locTo: "Jl. Raya Daan Mogot No.2, RT.2/RW.4, Kalideres, Kec. Kalideres, Kota Jakarta Barat",
        timePickUp: 1664557200000,
        timeDropOff: 1674458200000,
        time1: 1664557200000,
        time2: 1664557200000,
        time3: 1674358200000,
        time4: 1674558200000,
        dataCargo:{
            type: "A",
            weight: "1000",
            volumetric: "1000",
            length: "2000",
            width: "400",
            height: "150",
            desc: "test test test",
            remarks: "test test test"
        },
        dataRemarks:{
            remarks: "test test test",
            special: "test test test"
        }
    },
    {
        jobId: "CKCTJ2023101900003",
        jobType: "DOMESTIC",
        locFrom: "The Prime Office Suite Yos Sudarso Kav. 30 Street, 7th Floor, RT.10/RW.11, Sunter Jaya, Kec. Tj. Priok, Jkt Utara, Daerah Khusus Ibukota Jakarta 14330",
        locTo: "Jl. Raya Daan Mogot No.2, RT.2/RW.4, Kalideres, Kec. Kalideres, Kota Jakarta Barat",
        timePickUp: 1664557200000,
        timeDropOff: 1674458200000,
        time1: 1664557200000,
        time2: 1664557200000,
        time3: 1674358200000,
        time4: 1674558200000,
        dataCargo:{
            type: "A",
            weight: "1000",
            volumetric: "1000",
            length: "2000",
            width: "400",
            height: "150",
            desc: "test test test",
            remarks: "test test test"
        },
        dataRemarks:{
            remarks: "test test test",
            special: "test test test"
        }
    },
    {
        jobId: "CKCTJ2023101900004",
        jobType: "DOMESTIC",
        locFrom: "The Prime Office Suite Yos Sudarso Kav. 30 Street, 7th Floor, RT.10/RW.11, Sunter Jaya, Kec. Tj. Priok, Jkt Utara, Daerah Khusus Ibukota Jakarta 14330",
        locTo: "Jl. Raya Daan Mogot No.2, RT.2/RW.4, Kalideres, Kec. Kalideres, Kota Jakarta Barat",
        timePickUp: 1664557200000,
        timeDropOff: 1674458200000,
        time1: 1664557200000,
        time2: 1664557200000,
        time3: 1674358200000,
        time4: 1674558200000,
        dataCargo:{
            type: "A",
            weight: "1000",
            volumetric: "1000",
            length: "2000",
            width: "400",
            height: "150",
            desc: "test test test",
            remarks: "test test test"
        },
        dataRemarks:{
            remarks: "test test test",
            special: "test test test"
        }
    },
] 