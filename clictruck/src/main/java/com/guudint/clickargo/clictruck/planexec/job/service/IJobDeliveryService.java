package com.guudint.clickargo.clictruck.planexec.job.service;

import java.util.ArrayList;
import java.util.List;

import com.guudint.clickargo.clictruck.track.dto.DistanceMatrixDto;
import com.guudint.clickargo.clictruck.track.dto.ImeiLatestStatusDto;
import com.guudint.clickargo.clictruck.track.dto.StarSenderWhatsappResponseDto;
import com.vcc.camelone.common.exception.ParameterException;

public interface IJobDeliveryService {
	
	/**
	 * Get Imei latest locaiton.
	 * @param iMei
	 * @param jobTruckId: Optional parameter, only for audit.
	 * @return GPS position, 
	 */
	public List<ImeiLatestStatusDto> getImeiLocation(String iMei, String jobTruckId) throws Exception;
	
	/**
	 * 
	 * @param origin: Location name: New York City NY, or GPS: -6.142520947173755, 106.89031560733623
	 * @param destination: Location name: Washington DC or GPS: -6.1568238821711585, 106.91053992823653
	 * @param jobId: Optional parameter, only for audit.
	 * @return like 
	 * <pre>
	 * {
		    "destination_addresses": [
		        "RWV6+66R, Jl. Raya Gading Indah, RT.13/RW.18, Klp. Gading Tim., Kec. Klp. Gading, Jkt Utara, Daerah Khusus Ibukota Jakarta 14240, Indonesia"
		    ],
		    "origin_addresses": [
		        "Jl. Yos Sudarso No.30, RT.10/RW.6, Sungai Bambu, Kec. Tj. Priok, Jkt Utara, Daerah Khusus Ibukota Jakarta 14360, Indonesia"
		    ],
		    "rows": [
		        {
		            "elements": [
		                {
		                    "distance": {
		                        "text": "2.7 mi",
		                        "value": 4389
		                    },
		                    "duration": {
		                        "text": "11 mins",
		                        "value": 685
		                    },
		                    "status": "OK"
		                }
		            ]
		        }
		    ],
		    "status": "OK"
		}
	 * </pre>
	 */
	public DistanceMatrixDto getDistanceMatrix(String origin, String destination, String jobId) throws Exception;
	
	/**
	 * 
	 * @param whatsappmobileNumber, [country code] + [mobile number], like: 6592345678, 6212345678901; 
	 * @param msgBody, less than 2K
	 * @param jobId: Optional parameter, only for audit.
	 * @return
	 * @throws ParameterException
	 */
	public StarSenderWhatsappResponseDto sendWhatAppMsg(String whatsappmobileNumber, String msgBody, String jobId) throws ParameterException;
	void sendYCloudWhatAppMsg (String clasQuinContactNo, String whatsappMobileNumber, ArrayList<String> msgBody, String jobId, String templateName) throws ParameterException;

}
