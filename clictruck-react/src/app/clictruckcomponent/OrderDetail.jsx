
import React, { Component } from 'react'
import CardMedia from "@material-ui/core/CardMedia";

export default class OrderDetail extends Component {
  render() {
// export default function OrderDetail(props){

    const { resdata, accnlogo } = this.props;

    const tdstyle = { textAlign: 'left', padding: '8px' };
    const tdstyle1 = { textAlign: 'left', padding: '8px',width: '30%' };
    const tdstyle2 = { textAlign: 'left', padding: '8px',width: '10%' };
    
    if(!resdata) return null;
    
    return (
    <div>
    {resdata?.orderDetails?.map(v => (
    <div style={{width : "210mm", minHeight: "297mm",  padding: "10mm", margin: "0 auto", border: '1px solid #dddddd'}} key={v.generalFields.jobNo}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <CardMedia
              style={{
                width: '130px',
                height: 'auto'
              }}
              component="img"
              image={`data:image;base64, ${accnlogo}`}
          />
          <p style={{ textAlign: 'center', color: '#3e80ba', fontSize: "16px" }}>ORDERS DETAIL</p>
        </div>
  
        <p style={{ textAlign: 'left', color: '#3e80ba', fontSize: "14px" }}>DRIVER USERNAME: {v.generalFields.username}</p>
  
        <div style={{marginTop: '20px', borderTop: '1px solid #dddddd'}}/>
  
        <p>JOB NUMBER</p>
        <p>{v.generalFields.jobNo}</p>
  
        <table style={{ width: '100%', borderCollapse: 'collapse', marginTop: '20px' }}>
          <thead>
            <tr>
              <th colSpan="3" style={{ border: '1px solid #dddddd', padding: '8px', backgroundColor: '#eeeeee', textAlign: 'center' }}>
                    Origin And Destination Information
              </th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td style={tdstyle1}>From</td>
              <td style={tdstyle2}>:</td>
              <td style={tdstyle}>{v.locationFields.locationFrom}</td>
            </tr>
            <tr>
              <td style={tdstyle1}>To</td>
              <td style={tdstyle2}>:</td>
              <td style={tdstyle}>{v.locationFields.locationTo}</td>
            </tr>
          </tbody>
        </table>
        <table style={{ width: '100%', borderCollapse: 'collapse', marginTop: '20px' }}>
          <thead>
            <tr>
              <th colSpan="3" style={{ border: '1px solid #dddddd', padding: '8px', backgroundColor: '#eeeeee', textAlign: 'center' }}>
                    General Information
              </th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td style={tdstyle1}>Customer Reference No</td>
              <td style={tdstyle2}>:</td>
              <td style={tdstyle}>{v.generalFields.cusRefNo}</td>
            </tr>
            <tr>
              <td style={tdstyle1}>Type</td>
              <td style={tdstyle2}>:</td>
              <td style={tdstyle}>{v.generalFields.shipmentType}</td>
            </tr>
            <tr>
              <td style={tdstyle1}>Plan Date</td>
              <td style={tdstyle2}>:</td>
              <td style={tdstyle}>{v.generalFields.planDate}</td>
            </tr>
            <tr>
              <td style={tdstyle1}>Booking Date</td>
              <td style={tdstyle2}>:</td>
              <td style={tdstyle}>{v.generalFields.bookDate}</td>
            </tr>
            <tr>
              <td style={tdstyle1}>Datetime of Delivery</td>
              <td style={tdstyle2}>:</td>
              <td style={tdstyle}>{v.generalFields.deliveryDate}</td>
            </tr>
            <tr>
              <td style={tdstyle1}>Loading</td>
              <td style={tdstyle2}>:</td>
              <td style={tdstyle}>{v.generalFields.loading}</td>
            </tr>
            <tr>
              <td style={tdstyle1}>Est. Pricing (SGD)</td>
              <td style={tdstyle2}>:</td>
              <td style={tdstyle}>{v.generalFields.estimatedPricing}</td>
            </tr>
            <tr>
              <td style={tdstyle1}>Email Notification</td>
              <td style={tdstyle2}>:</td>
              <td style={tdstyle}>{v.generalFields.emailNotif}</td>
            </tr>
            <tr>
              <td style={tdstyle1}>Goods Information</td>
              <td style={tdstyle2}>:</td>
              <td style={tdstyle}>{v.generalFields.goodsInfo}</td>
            </tr>
          </tbody>
        </table>
        <table style={{ width: '100%', borderCollapse: 'collapse', marginTop: '20px' }}>
          <thead>
            <tr>
              <th colSpan="4" style={{ border: '1px solid #dddddd', padding: '8px', backgroundColor: '#eeeeee', textAlign: 'center' }}>
                    Cargo Information
              </th>
            </tr>
          </thead>
          
          {v.cargoFieldsList.map(c => (
          <tbody key={c.seq}>
            <tr>
              <td style={tdstyle2}>{c.seq}</td>
              <td style={tdstyle1}>Type</td>
              <td style={tdstyle2}>:</td>
              <td style={tdstyle}>{c.cargoType}</td>
            </tr>
            <tr>
              <td style={tdstyle2}></td>
              <td style={tdstyle1}>Size</td>
              <td style={tdstyle2}>:</td>
              <td style={tdstyle}>{c.size}</td>
            </tr>
            <tr>
              <td style={tdstyle2}></td>
              <td style={tdstyle1}>Weight</td>
              <td style={tdstyle2}>:</td>
              <td style={tdstyle}>{c.weight}</td>
            </tr>
            <tr>
              <td style={tdstyle2}></td>
              <td style={tdstyle1}>Volumetric Weight</td>
              <td style={tdstyle2}>:</td>
              <td style={tdstyle}>{c.volWeight}</td>
            </tr>
            <tr>
              <td style={tdstyle2}></td>
              <td style={tdstyle1}>Truck Type</td>
              <td style={tdstyle2}>:</td>
              <td style={tdstyle}>{c.truckType}</td>
            </tr>
            <tr>
              <td style={tdstyle2}></td>
              <td style={tdstyle1}>Status</td>
              <td style={tdstyle2}>:</td>
              <td style={tdstyle}>{c.status}</td>
            </tr>
          </tbody>
          ))}
          
        </table>
    </div>
    ))}
    </div>
    )
  }
}
