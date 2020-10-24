package com.n26.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class StatisticResponse  implements Serializable {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal sum ;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal avg ;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal max;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal min ;
    private long count ;

    public static Builder newBuilder() {
        return new Builder();
    }
    private StatisticResponse(Builder builder) {
        setSum(builder.sum);
        setAvg(builder.avg);
        setMax(builder.max);
        setMin(builder.min);
        setCount(builder.count);

    }

    /**
     * {@code StatisticResponse} builder static inner class.
     */
    public static final class Builder {
        private BigDecimal sum = new BigDecimal("0.00");
        private BigDecimal avg = new BigDecimal("0.00");
        private BigDecimal max= new BigDecimal("0.00");
        private BigDecimal min = new BigDecimal("0.00");
        private long count ;

        private Builder() {
        }

        /**
         * Sets the {@code sum} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param sum the {@code sum} to set
         * @return a reference to this Builder
         */
        public Builder withSum(BigDecimal sum) {
            this.sum = sum;
            return this;
        }

        /**
         * Sets the {@code avg} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param avg the {@code avg} to set
         * @return a reference to this Builder
         */
        public Builder withAvg(BigDecimal avg) {
            this.avg = avg;
            return this;
        }

        /**
         * Sets the {@code max} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param max the {@code max} to set
         * @return a reference to this Builder
         */
        public Builder withMax(BigDecimal max) {
            this.max = max;
            return this;
        }

        /**
         * Sets the {@code min} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param min the {@code min} to set
         * @return a reference to this Builder
         */
        public Builder withMin(BigDecimal min) {
            this.min = min;
            return this;
        }

        /**
         * Sets the {@code count} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param count the {@code count} to set
         * @return a reference to this Builder
         */
        public Builder withCount(long count) {
            this.count = count;
            return this;
        }

        /**
         * Returns a {@code StatisticResponse} built from the parameters previously set.
         *
         * @return a {@code StatisticResponse} built with parameters of this {@code StatisticResponse.Builder}
         */
        public StatisticResponse build() {
            return new StatisticResponse(this);
        }
    }
}
