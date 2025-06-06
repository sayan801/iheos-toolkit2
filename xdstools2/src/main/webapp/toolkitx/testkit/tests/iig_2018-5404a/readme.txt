Xfer Syntax Failure, Single Responding Gateway
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <meta http-equiv="content-type" content="text/html;
      charset=windows-1252">
    <title>Initiating Imaging Gateway: Transfer Syntax Failure, Single
      Responding Gateway</title>
  </head>
  <body>
    <h2>Transfer Syntax Failure, Single Responding Gateway</h2>
    <p>Tests the ability of the Initiating Imaging Gateway actor (SUT)
      to respond correctly to Retrieve Image Document Set (RAD-69)
      Request from an Image Document Consumer actor (Simulator), for a
      single DICOM image file using the JPEG Baseline (Process 1)
      (1.2.840.10008.1.2.4.50) Transfer Syntax, in the case where the
      Image Document Source is not able to return the image file in that
      Syntax. </p>
    <h3>Retrieve Parameters</h3>
    <table border="1">
      <tbody>
        <tr>
          <td>RIG Home Community ID (A)</td>
          <td>urn:oid:1.3.6.1.4.1.21367.13.70.101</td>
        </tr>
        <tr>
          <td>IDS Repository Unique ID (A1)</td>
          <td>1.3.6.1.4.1.21367.13.71.101</td>
        </tr>
        <tr>
          <td>Transfer Syntax UID</td>
          <td>1.2.840.10008.1.2.4.50</td>
        </tr>
      </tbody>
    </table>
    <p><b>Note:</b> Although the test environment provides for three
      Responding Imaging Gateways and multiple Image Document Sources,
      this test expects a single image to be returned from a single
      Responding Imaging Gateway (A) and a single Imaging Document
      Source (A1) behind that gateway.<br>
    </p>
    <h3>Test Execution</h3>
    <p>The test consists of three steps: </p>
    <ol>
      <li>Test software sends RAD-69 request to System Under test and
        records response.</li>
      <ul>
        <li>System Under Test sends a RAD-75 request to Responding
          Imaging Gateway which stores the request.</li>
        <li>Responding Imaging Gateway provides RAD-75 response to
          System Under Test.</li>
        <li>System Under Test provides RAD-69 response to test software.<br>
        </li>
      </ul>
      <li>Test software validates the RAD-75 request that is sent by the
        System Under Test.</li>
      <li>Test software validates the RAD-69 response sent by the System
        Under Test.</li>
    </ol>
  </body>
</html>
