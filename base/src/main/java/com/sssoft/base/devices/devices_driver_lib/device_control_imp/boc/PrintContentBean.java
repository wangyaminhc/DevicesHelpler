package com.sssoft.base.devices.devices_driver_lib.device_control_imp.boc;

import java.util.List;


public class PrintContentBean {

    private List<SposBean> spos;

    public List<SposBean> getSpos() {
        return spos;
    }

    public void setSpos(List<SposBean> spos) {
        this.spos = spos;
    }

    public static class SposBean {
        /**
         * content-type : txt
         * size : 2
         * content : 示例文
         字
         * position : left
         * offset : 40
         * bold : 1
         * height : 50
         */
        @com.google.gson.annotations.SerializedName("content-type")
        private String contenttype;
        private String size;
        private String content;
        private String position;
        private String offset="0";
        private String bold="0";
        private String height;

        public String getContenttype() {
            return contenttype;
        }

        public void setContenttype(String contenttype) {
            this.contenttype = contenttype;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public String getOffset() {
            return offset;
        }

        public void setOffset(String offset) {
            this.offset = offset;
        }

        public String getBold() {
            return bold;
        }

        public void setBold(String bold) {
            this.bold = bold;
        }

        public String getHeight() {
            return height;
        }

        public void setHeight(String height) {
            this.height = height;
        }
    }
}
