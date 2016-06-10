package a4.api;


/*
 *------------------------------------------------------------------------------
 * INF WP B3 AZ4 A4 SS16
 * 
 * VCS: git@BitBucket.org:schaefers/WPPPJ-CRC.git
 *                                                Michael Schaefers  2016/05/17
 *------------------------------------------------------------------------------
 */
/**
 * Das Interface {@link Item_I}
 * <ul>
 *     <li>beschreibt ein Item und</li>
 *     <li>definiert die Funktionalit&auml;t m&ouml;glicher Implementierungen und
 *         fordert die entsprechenden Methoden ein.</li>
 * </ul>
 * Die von Ihnen zu implementierende Klasse Item muss
 * <ul>
 *     <li>sich wie ein Item verhalten. Dies war Thema der Vorlesung.
 *     </li>
 * </ul>
 */
public interface Item_I {
    
    /**
     * Die Methode {@link #getFileName()} liefert den Namen der jeweiligen Datei.
     * @return Dateiname der im Item erfassten Datei.
     */
    public String getFileName();
    
    /**
     * Die Methode {@link #getFileSize()} liefert die (physikalische) Dateigr&ouml;&szlig;e der jeweiligen Datei.
     * @return Dateigr&ouml;&szlig;e der im Item erfassten Datei.
     */
    public long getFileSize();
    
    /**
     * Die Methode {@link #getSignature(int)} liefert die Signatur (bzw. den CRC) 
     * zum jeweils &uuml;ber den Parameter bestimmten Polynom f�r die jeweilige im Item erfassten Datei.
     * Es m&uuml;ssen mindesten die folgenden Polynome unterst�tzt werden.
     * <table>
     *   <tr> <td></td>                    <td align="center">Polynom</td>    <td></td>        <td align="center">Polynom-ID</td>  </tr>
     *   <tr> <td valign="top">&bull;</td> <td><code>0x1000000af</code></td>  <td>&harr;</td>  <td align="center">0<td> </tr>
     *   <tr> <td valign="top">&bull;</td> <td><code>0x100400007</code></td>  <td>&harr;</td>  <td align="center">1<td> </tr>
     *   <tr> <td valign="top">&bull;</td> <td><code>0x104c11db7</code></td>  <td>&harr;</td>  <td align="center">2<td> </tr>
     *   <tr> <td valign="top">&bull;</td> <td><code>0x127673637</code></td>  <td>&harr;</td>  <td align="center">3<td> </tr>
     * </table>
     * @param signatureId die ID zur Signatur bzw. die ID des zugrunde liegenden Polynoms.
     *                    F&uuml;r die Definition der Polynom-ID siehe bei den Erkl&auml;rungen zur Methode {@link #getSignature(int)}.
     * @return Signatur (bzw. CRC) der im Item erfassten Datei zum jeweiligen &uml;ber den Parameter bestimmten Polynom.
     */
    public int getSignature( final int signatureId );

}//interface
