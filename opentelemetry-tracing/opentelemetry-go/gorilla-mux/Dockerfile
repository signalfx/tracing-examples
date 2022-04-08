FROM golang:1.18 AS builder
WORKDIR /app
COPY go.* ./
RUN go mod download
COPY *.go .
RUN CGO_ENABLED=0 go build -o app

FROM scratch
COPY --from=builder /etc/ssl/certs/ca-certificates.crt /etc/ssl/certs/
COPY --from=builder /app/app /app
ENTRYPOINT ["/app"]
