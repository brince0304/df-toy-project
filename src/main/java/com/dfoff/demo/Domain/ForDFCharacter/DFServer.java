package com.dfoff.demo.Domain.ForDFCharacter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.*;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

@AllArgsConstructor
@Entity
@Getter
@ToString
@NoArgsConstructor (access = AccessLevel.PROTECTED)
@Builder
@EqualsAndHashCode(of = "serverId")
public class DFServer {
    @Id
    String serverId;
    @Setter
    @Column (nullable = false)
    String serverName;

    @javax.annotation.Generated("jsonschema2pojo")
    public static class DFServerDTO {

        @SerializedName("rows")
        @Expose
        private List<Row> rows = null;

        /**
         * No args constructor for use in serialization
         *
         */
        public DFServerDTO() {
        }

        /**
         *
         * @param rows
         */
        public DFServerDTO(List<Row> rows) {
            super();
            this.rows = rows;
        }

        public List<Row> getRows() {
            return rows;
        }

        public void setRows(List<Row> rows) {
            this.rows = rows;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(DFServerDTO.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
            sb.append("rows");
            sb.append('=');
            sb.append(((this.rows == null)?"<null>":this.rows));
            sb.append(',');
            if (sb.charAt((sb.length()- 1)) == ',') {
                sb.setCharAt((sb.length()- 1), ']');
            } else {
                sb.append(']');
            }
            return sb.toString();
        }

        @Override
        public int hashCode() {
            int result = 1;
            result = ((result* 31)+((this.rows == null)? 0 :this.rows.hashCode()));
            return result;
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }
            if ((other instanceof DFServerDTO) == false) {
                return false;
            }
            DFServerDTO rhs = ((DFServerDTO) other);
            return ((this.rows == rhs.rows)||((this.rows!= null)&&this.rows.equals(rhs.rows)));
        }

        @Generated("jsonschema2pojo")
        public static class Row {

            @SerializedName("serverId")
            @Expose
            private String serverId;
            @SerializedName("serverName")
            @Expose
            private String serverName;

            /**
             * No args constructor for use in serialization
             *
             */
            public Row() {
            }

            /**
             *
             * @param serverName
             * @param serverId
             */
            public Row(String serverId, String serverName) {
                super();
                this.serverId = serverId;
                this.serverName = serverName;
            }

            public String getServerId() {
                return serverId;
            }

            public void setServerId(String serverId) {
                this.serverId = serverId;
            }

            public String getServerName() {
                return serverName;
            }

            public void setServerName(String serverName) {
                this.serverName = serverName;
            }

            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder();
                sb.append(Row.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
                sb.append("serverId");
                sb.append('=');
                sb.append(((this.serverId == null)?"<null>":this.serverId));
                sb.append(',');
                sb.append("serverName");
                sb.append('=');
                sb.append(((this.serverName == null)?"<null>":this.serverName));
                sb.append(',');
                if (sb.charAt((sb.length()- 1)) == ',') {
                    sb.setCharAt((sb.length()- 1), ']');
                } else {
                    sb.append(']');
                }
                return sb.toString();
            }

            @Override
            public int hashCode() {
                int result = 1;
                result = ((result* 31)+((this.serverId == null)? 0 :this.serverId.hashCode()));
                result = ((result* 31)+((this.serverName == null)? 0 :this.serverName.hashCode()));
                return result;
            }

            @Override
            public boolean equals(Object other) {
                if (other == this) {
                    return true;
                }
                if ((other instanceof Row) == false) {
                    return false;
                }
                Row rhs = ((Row) other);
                return (((this.serverId == rhs.serverId)||((this.serverId!= null)&&this.serverId.equals(rhs.serverId)))&&((this.serverName == rhs.serverName)||((this.serverName!= null)&&this.serverName.equals(rhs.serverName))));
            }

        }
    }
}
