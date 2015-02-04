package org.graphast.query.knn.model;

	public class QueueEntry implements Comparable<QueueEntry> {
		private long id;
		private short travelTime;

		public QueueEntry(long id, short travelTime) {
			this.id = id;
			this.travelTime = travelTime;
		}

		public int compareTo(QueueEntry another) {
			return new Short(this.travelTime).compareTo(another.getTravelTime());
		}
		
		@Override
		public boolean equals(Object o){
			return this.id == ((QueueEntry)o).id;
		}
		
		public String toString(){
			return "( ID:"+id+" TT:"+travelTime+" )";
		}

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public short getTravelTime() {
			return travelTime;
		}

		public void setTravelTime(short travelTime) {
			this.travelTime = travelTime;
		}
	}
