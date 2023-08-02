import csv

def create_hashmap_from_records(records):
    hashmap = {}

    for record in records:
        parts = record.split(" : ")
        if len(parts) == 3:
            source_type, version, data = parts
            hashmap_key = f"{source_type} : {int(float(version))}"
            hashmap[hashmap_key] = data.strip()

    return hashmap

def read_records_from_file(filename):
    with open(filename, "r") as file:
        records = file.readlines()
    return records

filename = "raw_results.txt"
records = read_records_from_file(filename)

hashmap = create_hashmap_from_records(records)


# get the latency for the first node
results = []
senml_latency = []
for i in range(1, 11):
    key_source = f"SOURCE : {str(i)}"
    key_dest = f"SENML : {str(i)}"
    senml_latency.append(int(hashmap[key_dest]) - int(hashmap[key_source]))
results.append(senml_latency)

range_latency = []
for i in range(1, 11):
    key_source = f"SENML : {str(i)}"
    key_dest = f"RANGE : {str(i)}"
    range_latency.append(int(hashmap[key_dest]) - int(hashmap[key_source]))
results.append(range_latency)

bloom_latency = []
for i in range(1, 11):
    key_source = f"RANGE : {str(i)}"
    key_dest = f"BLOOM : {str(i)}"
    bloom_latency.append(int(hashmap[key_dest]) - int(hashmap[key_source]))
results.append(bloom_latency)

inter_latency = []
for i in range(1, 11):
    key_source = f"BLOOM : {str(i)}"
    key_dest = f"INTER : {str(i)}"
    inter_latency.append(int(hashmap[key_dest]) - int(hashmap[key_source]))
results.append(inter_latency)

annot_latency = []
for i in range(1, 11):
    key_source = f"INTER : {str(i)}"
    key_dest = f"ANNOT : {str(i)}"
    annot_latency.append(int(hashmap[key_dest]) - int(hashmap[key_source]))
results.append(annot_latency)

storage_latency = []
for i in range(1, 11):
    key_source = f"ANNOT : {str(i)}"
    key_dest = f"STORAGE : {str(i)}"
    storage_latency.append(int(hashmap[key_dest]) - int(hashmap[key_source]))
results.append(storage_latency)

with open("results.csv", mode="w", newline='') as file:
    writer = csv.writer(file)
    writer.writerow(["SENML", "RANGE", "BLOOM", "INTERPOLATE", "ANNOT", "PERSIST", "TOTAL_SUM"])
    avg = []
    for i in range(10):
        row_arr = []
        for arr in results:
            row_arr.append(arr[i]/1000000)
        row_arr.append(sum(row_arr))
        writer.writerow(row_arr)

print(avg)
